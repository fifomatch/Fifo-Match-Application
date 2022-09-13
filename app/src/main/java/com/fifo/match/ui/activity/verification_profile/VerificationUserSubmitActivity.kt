package com.fifo.match.ui.activity.verification_profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toFile
import com.fifo.match.R
import com.fifo.match.databinding.VerificationSubmitBinding
import com.fifo.match.network.NetworkResult
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class VerificationUserSubmitActivity :
    BaseActivity<VerificationViewModel, VerificationSubmitBinding>(VerificationViewModel::class.java) {

    private val PROFILE_PIC_REQUEST_CODE = 109
    private var profileUri: File? = null

    private val viewModel by viewModels<VerificationViewModel>()
    override fun getLayoutRes() = R.layout.verification_submit

    override fun initView() {
        transparentStatusBar()
        binding.activity = this
        registerObservers()

        binding.btnSubmit.setOnClickListener {
            ImagePicker.with(this).crop().compress(1024).start(PROFILE_PIC_REQUEST_CODE)
        }
        binding.btnRetake.setOnClickListener {
            ImagePicker.with(this).crop().compress(1024).start(PROFILE_PIC_REQUEST_CODE)
        }

        binding.btnSubmitFinal.setOnClickListener {
            if (profileUri != null) {
                viewModel.callUserVerifyApi(profileUri!!)
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data!!
            when (requestCode) {
                PROFILE_PIC_REQUEST_CODE -> {
                    profileUri = uri.toFile()
                    binding.layPhoto.visible()
                    binding.imgCrossPhoto.setLocalImage(uri)
                    binding.txtHeader.text = getString(R.string.do_they_match)
                    binding.txtHeaderBelow.text = getString(R.string.if_your_photo)
                    binding.btnSubmit.visibility = View.GONE
                    binding.btnSubmitFinal.visibility = View.VISIBLE
                    binding.btnSubmitFinal.text = getString(R.string.submit)
                    binding.btnRetake.visibility = View.VISIBLE
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            binding.layPhoto.gone()
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            binding.layPhoto.gone()
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
                   onBackPressed()
                    response.data?.result?.let {
                    }
                }
                is NetworkResult.Error -> {
                    ShowProgressDialog.hideProgress()
                    toastError(response.exception.localizedMessage!!.toString())

                }

            }
        }
    }


}