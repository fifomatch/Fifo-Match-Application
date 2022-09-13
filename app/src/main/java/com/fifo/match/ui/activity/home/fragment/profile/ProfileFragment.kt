package com.fifo.match.ui.activity.home.fragment.profile

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.fifo.match.R
import com.fifo.match.databinding.BottomsheetLogoutBinding
import com.fifo.match.databinding.FragmentProfileBinding
import com.fifo.match.model.CheckPlanModel
import com.fifo.match.model.ProfileModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.utils.Constants
import com.fifo.match.ui.activity.appearance.AppearanceActivity
import com.fifo.match.ui.activity.editProfile.EditProfileActivity
import com.fifo.match.ui.activity.editProfile.ImageEditActivity
import com.fifo.match.ui.activity.hiddenProfile.HiddenProfilesActivity
import com.fifo.match.ui.activity.membership.MembershipActivity
import com.fifo.match.ui.activity.profileSurvey.ProfileSurveyActivity
import com.fifo.match.ui.activity.removeAccount.RemoveAccountActivity
import com.fifo.match.ui.activity.setting.SettingActivity
import com.fifo.match.ui.activity.upgradeMembership.UpgradeMembershipActivity
import com.fifo.match.ui.activity.verification_profile.VerificationUserActivity
import com.fifo.match.ui.activity.verification_profile.VerificationUserSubmitActivity
import com.fifo.match.ui.activity.viewYourProfile.ViewYourProfileActivity
import com.fifo.match.ui.activity.welcome.WelcomeActivity
import com.fifo.match.ui.base.BaseFragment
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import com.github.hariprasanths.bounceview.BounceView
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class ProfileFragment : BaseFragment<ProfileViewModel, FragmentProfileBinding>(ProfileViewModel::class.java)   {

    var isPlanPremium = false
    var isPlan = false

    private var myProfile: ProfileModel? = null

    private val viewModel: ProfileViewModel by viewModels()
    override fun getLayoutRes() = R.layout.fragment_profile

    override fun onRender() {
        super.onRender()
        binding.fragment = this
        animationButton()
        viewModel.callActivePlan()
        planObserver()
        viewModel.getMyProfile()
        getMyProfileObservers()
        getSignOutObservers()

    }

    private fun animationButton() {
        BounceView.addAnimTo(binding.llLogout)
        BounceView.addAnimTo(binding.rlVerifi)
        BounceView.addAnimTo(binding.rlHidden)
        BounceView.addAnimTo(binding.rlRemove)
        BounceView.addAnimTo(binding.rlSetting)
        BounceView.addAnimTo(binding.rlViewProfile)
        BounceView.addAnimTo(binding.rlAppearance)
        BounceView.addAnimTo(binding.rlSurvey)
        BounceView.addAnimTo(binding.rlMem)
        BounceView.addAnimTo(binding.rlPersonalInfo)
    }

    fun onNext(int: Int) {
        when (int) {
            0 -> {
                requireActivity().openActivity<ImageEditActivity>(Constants.PHOTOS to myProfile?.photos!!)
            }
            1 -> {
                requireActivity().openActivity<EditProfileActivity>()
            }
            2 -> {
                requireActivity().openActivity<AppearanceActivity>()
            }
            3 -> {
                requireActivity().openActivity<ProfileSurveyActivity>()
            }
            4 -> {
                if (isPlan){
//                    requireActivity().openActivity<MembershipActivity>()
                    requireActivity().openActivity<UpgradeMembershipActivity>()
                }else{
                    requireActivity().openActivity<MembershipActivity>()
                }
            }
            5 -> {
                if (isPlanPremium) {
                    requireActivity().openActivity<ViewYourProfileActivity>()
                } else {
                    alertDialog { this.setMessage("Please purchase premium Plan") }
                }
            }
            6 -> {
                requireActivity().openActivity<SettingActivity>()
            }
            7 -> {
                requireActivity().openActivity<RemoveAccountActivity>()
            }
            8 -> {
                requireActivity().openActivity<HiddenProfilesActivity>()
            }
            9 -> {
                bottomSheetLogOut()
            }
            10->{
                if (myProfile?.verify == 0){
                requireActivity().openActivity<VerificationUserSubmitActivity>()
                }else{
                    requireActivity().openActivity<VerificationUserActivity>(Constants.PROFILE_VERIFY to myProfile?.verify.toString())
                }
            }
        }
    }

    private fun bottomSheetLogOut() {
        val binding: BottomsheetLogoutBinding = DataBindingUtil.inflate(LayoutInflater.from(requireActivity()), R.layout.bottomsheet_logout, null, false)
        val dialog = BottomSheetDialog(requireActivity(), R.style.AppBottomSheetDialogThemeXX)
        BounceView.addAnimTo(dialog);
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        binding.btnLogout.roundBorderedViewFromResId(15, R.color.white, R.color.white, 1)
        binding.btnCancel.roundBorderedViewFromResId(15, R.color.transperent, R.color.white, 1)
        binding.btnLogout.setOnClickListener {
            viewModel.stSignOut.set("0")
            viewModel.callSignOutApi()
            dialog.dismiss()
        }
        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(binding.root)
        dialog.show()
    }

    private fun planObserver() {
        viewModel.responsePlan.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(activity)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<CheckPlanModel>(it) {
                            viewModel.stCheckPlan.value = this
                            getPlanDetails(this)
                        }
                    }
                    if (response.data!!.result == null){
                        isPlan = false
                    }
                }
                is NetworkResult.Error -> {
                    ShowProgressDialog.hideProgress()
                    if (response.exception.localizedMessage!!.toString() == "Token is Invalid"){
                        requireActivity().openActivity<WelcomeActivity>()
                        requireActivity().finish()
                        lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.clearSession()
                        }
                    }
                }
            }
        }
    }

    private fun getPlanDetails(checkPlanModel: CheckPlanModel) {
        isPlan = true
        val currentString = checkPlanModel.endDate
        val separated = currentString?.split("T")!!.toTypedArray()
        val planEndDate = separated[0]
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = sdf.format(Date())
        if (checkPlanModel.planId.equals("plan_id_01")) {
            isPlanPremium = false
            binding.ivPlanImage.setImageResource(R.drawable.ic_plan_logo)
        } else if (checkPlanModel.planId.equals("plan_id_02")) {
            isPlanPremium = true
            binding.ivPlanImage.setImageResource(R.drawable.ic_premium)
        }
        if (planEndDate < todayDate){
            isPlanPremium = false
        }
    }

    private fun getMyProfileObservers() {
        viewModel.responseMyProfile.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(activity)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    tryCast<ProfileModel>(response.data?.result) {
                        myProfile = this
                        myProfile?.let { setProfileData(it) }
                    }
                }
                is NetworkResult.Error -> {
                    ShowProgressDialog.hideProgress()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setProfileData(data: ProfileModel) {
        binding.apply {
            tvName.text = data?.name + " " + data!!.age.toString()
            tvDistance.text = userData?.countryName
            if (data.photos?.get(0)?.name != null) {
                Glide.with(requireActivity()).load(data.photos[0]?.name.toString()).into(ivimage)
            }

            val date =  parseDateToddMMyyyy(data.dob.toString())
            tvDob.text = "Born on: $date"

            if (data.verify != null){
                if (data.verify == 2){
                    ivVerify.visible()
                }else{
                    ivVerify.gone()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyProfile()
    }

    private fun getSignOutObservers() {
        viewModel.responseSignOut.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(activity)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    response.data.let {
                        requireActivity().openActivity<WelcomeActivity>()
                        lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.clearSession()
                        }
                        requireActivity().finish()
                    }

                }
                is NetworkResult.Error -> {
                    ShowProgressDialog.hideProgress()
                }
            }
        }
    }
}