package com.fifo.match.ui.activity.editProfile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toFile
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.fifo.match.R
import com.fifo.match.databinding.ActivityImageEditBinding
import com.fifo.match.model.ProfileModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.utils.Constants
import com.fifo.match.ui.activity.completeProfile.CompleteProfileViewModel
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.math.log

@AndroidEntryPoint
class ImageEditActivity : BaseActivity<CompleteProfileViewModel, ActivityImageEditBinding>(CompleteProfileViewModel::class.java) {

    private lateinit var listPhots: ArrayList<ProfileModel.Photo>
    private var updatePhotoList = ArrayList<File>()
    private lateinit var adapter: ImageEditAdapter
    private var mSelectedPos = 0
    private val RECYCLER_VIEW_PIC_REQUEST_CODE = 209

    private val viewModel by viewModels<CompleteProfileViewModel>()

    override fun getLayoutRes() = R.layout.activity_image_edit

    override fun initView() {
        transparentStatusBar()
        binding.activity = this

        binding.btnSignIn.roundBorderedViewFromResId(15, R.color.orange, R.color.orange, 1)

        if (intent.hasExtra(Constants.PHOTOS)) {
            val myProfile = intent.getSerializableExtra(Constants.PHOTOS)
            listPhots = myProfile as ArrayList<ProfileModel.Photo>
            Glide.with(this).load(listPhots[0].name).into(binding.ivImage)
        }

        dataObservers()
        deletePhotoObservers()
        listPhots.removeAt(0)
        adapter = ImageEditAdapter(listPhots,  object : ImageEditAdapter.GetClick {
            override fun getClick(int: Int,position: Int,number:Int) {
                mSelectedPos = position
                when(number){
                    1->{
                        viewModel.getDeletePhotos(int)
                    }
                    2->{
                        toastInfo("Please upload 3 Image after delete Image")
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun pickImageClick(position: Int) {
                mSelectedPos = position
                ImagePicker.with(this@ImageEditActivity).crop().compress(1024).maxResultSize(1080, 1080).start(RECYCLER_VIEW_PIC_REQUEST_CODE)
            }
        })
        binding.rvImage.layoutManager = GridLayoutManager(this, 3)
        binding.rvImage.adapter = adapter

        binding.btnSignIn.setOnClickListener { getPhotos() }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data!!
            when (requestCode) {
                RECYCLER_VIEW_PIC_REQUEST_CODE -> {
                    listPhots.add(ProfileModel.Photo(-1,-1,-1,null,uri.toFile(),"","",""))
                    adapter.notifyDataSetChanged()
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getPhotos() {
        listPhots.forEachIndexed { index, photo ->
            if (listPhots[index].nameFile != null){
                updatePhotoList.add(photo.nameFile)
            }
        }
        if (updatePhotoList.isNotEmpty()) {
            viewModel.getProfilePhotos(updatePhotoList)
        } else {
            toast("please upload photos")
        }
    }

    private fun dataObservers() {
        viewModel.responsePhotos.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                }
                is NetworkResult.Error -> {
                    ShowProgressDialog.hideProgress()
                    toastError(response.exception.localizedMessage!!.toString())
                }
            }
        }
    }


    private fun deletePhotoObservers() {
        viewModel.responseDeletePhotos.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                }
                is NetworkResult.Error -> {
                    ShowProgressDialog.hideProgress()
                    toastError(response.exception.localizedMessage!!.toString())
                }
            }
        }
    }

}