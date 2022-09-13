package com.fifo.match.ui.activity.setting

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.fifo.match.R
import com.fifo.match.databinding.ActivitySettingBinding
import com.fifo.match.local.MyDataStore
import com.fifo.match.model.CheckPlanModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.utils.Constants
import com.fifo.match.network.utils.Constants.ABOUT_US
import com.fifo.match.network.utils.Constants.CONTACT_US
import com.fifo.match.network.utils.Constants.FAQ
import com.fifo.match.network.utils.Constants.PRIVACY_POLICY
import com.fifo.match.network.utils.Constants.TERMS_SERVICE
import com.fifo.match.ui.activity.aboutus.AboutsActivity
import com.fifo.match.ui.activity.home.fragment.profile.ProfileViewModel
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import com.github.hariprasanths.bounceview.BounceView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingActivity : BaseActivity<ProfileViewModel, ActivitySettingBinding>(ProfileViewModel::class.java) {

    @Inject
    lateinit var myDataStore: MyDataStore
    var isPlanPremium = false

    override fun getLayoutRes() = R.layout.activity_setting
    private val viewModel by viewModels<ProfileViewModel>()

    override fun initView() {
        transparentStatusBar()
        binding.activity = this
        onlineObserver()
        initListener()
        setAni()
        viewModel.callActivePlan()
        planObserver()
    }

    private fun setAni() {
        BounceView.addAnimTo(binding.rlAboutas)
        BounceView.addAnimTo(binding.rlCountas)
        BounceView.addAnimTo(binding.rlHelp)
        BounceView.addAnimTo(binding.rlpp)
        BounceView.addAnimTo(binding.rlTc)
    }

    private fun initListener() {
        setStatus()
        binding.scOnline.setOnClickListener {
            if (isPlanPremium) {
                if (binding.scOnline.isChecked) {
                    viewModel.stOnlineOffline.set("online")
                    viewModel.callOnlineOffline()
                } else {
                    viewModel.stOnlineOffline.set("offline")
                    viewModel.callOnlineOffline()
                }
            }else{
                binding.scOnline.isChecked=true
                alertDialog {
                    this.setMessage("Please purchase premium Plan")
                }
            }
        }

        binding.scNoti.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.stNotificationStatus.set("on")
                viewModel.callNotificationStatus()
            } else {
                viewModel.stNotificationStatus.set("off")
                viewModel.callNotificationStatus()
            }
        }
    }

    private fun setStatus() {
        binding.scOnline.isChecked = userData?.loginStatus.equals("online")
        binding.scNoti.isChecked = userData?.notificationStatus.equals("on")
    }

    fun onNext(int: Int) {
        when (int) {
            1 -> {
                openActivity<AboutsActivity>(Constants.WEB_FLAG to ABOUT_US)
            }
            2 -> {
                openActivity<AboutsActivity>(Constants.WEB_FLAG to CONTACT_US)
            }
            3 -> {
                openActivity<AboutsActivity>(Constants.WEB_FLAG to FAQ)
            }
            4 -> {
                openActivity<AboutsActivity>(Constants.WEB_FLAG to PRIVACY_POLICY)
            }
            5 -> {
                openActivity<AboutsActivity>(Constants.WEB_FLAG to TERMS_SERVICE)
            }
        }
    }

    private fun onlineObserver() {
        viewModel.responseOnline.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()

                    lifecycleScope.launch(Dispatchers.IO) {
                        if (binding.scOnline.isChecked) {
                            userData?.loginStatus = "online"
                            myDataStore.updateUser(userData!!)
                        } else {
                            userData?.loginStatus = "offline"
                            myDataStore.updateUser(userData!!)
                        }

                        if (binding.scNoti.isChecked) {
                            userData?.notificationStatus = "on"
                            myDataStore.updateUser(userData!!)
                        } else {
                            userData?.notificationStatus = "off"
                            myDataStore.updateUser(userData!!)
                        }

                    }

                }
                is NetworkResult.Error -> {
                    ShowProgressDialog.hideProgress()
                    toastError(response.exception.localizedMessage!!.toString())
                }
            }
        }
    }

    private fun planObserver() {
        viewModel.responsePlan.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<CheckPlanModel>(it) {
                            viewModel.stCheckPlan.value = this
                            getPlanDetails(this)
                        }
                    }
                }
                is NetworkResult.Error -> {
                    ShowProgressDialog.hideProgress()

                }
            }
        }
    }

    private fun getPlanDetails(checkPlanModel: CheckPlanModel) {
        if (checkPlanModel.planId.equals("plan_id_01")) {
            isPlanPremium = false
        } else if (checkPlanModel.planId.equals("plan_id_02")) {
            isPlanPremium = true
        }

        val currentString = checkPlanModel.endDate
        val separated = currentString?.split("T")!!.toTypedArray()
        val planEndDate = separated[0]
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = sdf.format(Date())

        if (planEndDate < todayDate){
            isPlanPremium = false
        }

    }


}