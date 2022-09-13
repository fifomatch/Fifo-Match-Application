package com.fifo.match.ui.activity.editProfile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toFile
import com.bumptech.glide.Glide
import com.fifo.match.R
import com.fifo.match.databinding.ActivityEditProfileBinding
import com.fifo.match.model.CompleteProfileListBean
import com.fifo.match.model.ProfileModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.MultipartHelper
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class EditProfileActivity : BaseActivity<EditProfileViewModel, ActivityEditProfileBinding>(EditProfileViewModel::class.java) {

    val listOfRelation = mutableListOf<String>()
    val listOfOccupationsData = mutableListOf<String>()
    val listOfEducation = mutableListOf<String>()

    val PROFILE_PIC_REQUEST_CODE = 109
    private var profileUri: File? = null
    var gender = arrayListOf("Man", "Woman","Transgender")
    var interested = arrayListOf("Man", "Woman", "Transgender")
    private var profileListBean: CompleteProfileListBean? = null
    private var myProfile: ProfileModel? = null

    override fun getLayoutRes() = R.layout.activity_edit_profile
    private val viewModel by viewModels<EditProfileViewModel>()

    override fun initView() {
        transparentStatusBar()
        binding.activity = this
        binding.viewModel = viewModel
        initListeners()
    }



    private fun initListeners() {
        binding.btnSave.roundBorderedViewFromResId(15, R.color.orange, R.color.orange, 1)
        binding.ssGender.setItem(gender)
        binding.ssInterset.setItem(interested)
        viewModel.getSpinnerDataList()
        registerObservers()
        viewModel.getMyProfile()
        getMyProfileObservers()
        profileUpdateObservers()

        binding.btnSave.setOnClickListener {
            viewModel.updateProfile()
        }

//        binding.etEmail.setText(userData?.email.toString())


        binding.etDOB.setOnClickListener {
            val cal = Calendar.getInstance()
            cal.add(Calendar.YEAR, -18)
            val dateToday = cal.time
            val sdf = SimpleDateFormat("yyyy/MM/dd")
            val dateforrow = sdf.format(dateToday)
            datePicker(this, binding.etDOB, dateforrow, isMinDate = false, isDOB = true)
        }

        viewModel.stDOB.set(binding.etDOB.text.toString())

        binding.ivEditSurvey.setOnClickListener {
            if (!viewModel.stEditProfile.get()) {
                binding.ivEditSurvey.snack(R.string.edit_mode, 2000) { }
                binding.btnSave.visible()
                binding.ivEditPic.visible()
                viewModel.stEditProfile.set(true)
            } else {
                binding.ivEditSurvey.snack(R.string.edit_mode_off, 2000) { }
                binding.btnSave.gone()
                binding.ivEditPic.gone()
                viewModel.stEditProfile.set(false)
            }
        }

        binding.ivEditPic.setOnClickListener {
            ImagePicker.with(this).crop().compress(1024).start(PROFILE_PIC_REQUEST_CODE)
        }

        binding.ssGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                viewModel.stGender.set(gender[position])
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        binding.ssInterset.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                viewModel.stInterested.set(interested[position].toString())
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data!!
            when (requestCode) {
                PROFILE_PIC_REQUEST_CODE -> {
                    profileUri = uri.toFile()
                    binding.profileImage.setLocalImage(uri)
                    viewModel.userProfilePhoto.add(MultipartHelper.prepareFilePart(this, "image", Uri.fromFile(profileUri), profileUri!!))
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerObservers() {
        viewModel.response.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    tryCast<CompleteProfileListBean>(response.data?.result) {
                        profileListBean = this
                        profileListBean?.let {
                            setSpinnerData(profileListBean!!)
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

    private fun setSpinnerData(profileListBean: CompleteProfileListBean) {

        profileListBean.relationshipStatusData?.forEach {
            listOfRelation.add(it?.name.toString())
        }
        profileListBean.occupationsData?.forEach {
            listOfOccupationsData.add(it?.name.toString())
        }
        profileListBean.educationData?.forEach {
            listOfEducation.add(it?.name.toString())
        }

        binding.etStatus.item = listOfRelation as List<String>
        binding.etOccupation.item = listOfOccupationsData as List<String>
        binding.etEducation.item = listOfEducation as List<String>

        binding.etStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                viewModel.stRelationshipStatus.set(profileListBean.relationshipStatusData?.get(position)?.id.toString())
            }
            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        binding.etOccupation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>, view: View,
                position: Int,
                id: Long
            ) {
                viewModel.stOccupations.set(profileListBean.occupationsData?.get(position)?.id.toString())
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        binding.etEducation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                viewModel.stEducation.set(profileListBean.educationData?.get(position)?.id.toString())
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

    }

    private fun getMyProfileObservers() {
        viewModel.responseMyProfile.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    tryCast<ProfileModel>(response.data?.result) {
                        myProfile = this
                        myProfile?.let {
                            setProfileData(it)
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

    private fun setProfileData(data: ProfileModel) {
        binding.apply {
            data.let {
                etFirstName.setText(it.name.toString())
                etDOB.setText(it.dob.toString())
                etMobileNumber.setText(it.mobile.toString())
                binding.etEmail.setText(it.email.toString())
                Glide.with(this@EditProfileActivity).load(it.photos?.get(0)?.name).into(profileImage)

                ssGender.post {
                    ssGender.setSelection(gender.indexOf(it.gender), true)
                }

                ssInterset.post {
                    ssInterset.setSelection(interested.indexOf(it.interestedIn), true)
                }

                etStatus.post {
                    etStatus.setSelection(it.relationshipData!!.id!!.toInt() - 1, true)
                }

                etOccupation.post {
                    etOccupation.setSelection(it.occupation?.id!!.toInt() - 1, true)
                }

                etEducation.post {
                    etEducation.setSelection(it.education?.id!!.toInt() - 1, true)
                }

                viewModel?.stRelationshipStatus?.set(it.relationshipData!!.id.toString())
                viewModel?.stOccupations?.set(it.occupation?.id.toString())
                viewModel?.stEducation?.set(it.education?.id.toString())
                viewModel?.stName?.set(it.name.toString())
                viewModel?.stDOB?.set(it.dob.toString())
                viewModel?.stGender?.set(it.gender)
                viewModel?.stInterested?.set(it.interestedIn)

            }

        }

    }

    private fun profileUpdateObservers() {
        viewModel.responseUpdateProfile.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    onBackPressed()
                }
                is NetworkResult.Error -> {
                    ShowProgressDialog.hideProgress()
                    toastError(response.exception.localizedMessage!!.toString())
                }
            }
        }
    }

}