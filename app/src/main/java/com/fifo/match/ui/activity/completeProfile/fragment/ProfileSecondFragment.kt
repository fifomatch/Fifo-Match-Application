package com.fifo.match.ui.activity.completeProfile.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toFile
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.fifo.match.R
import com.fifo.match.databinding.FragmentProfileSecoundBinding
import com.fifo.match.local.MyDataStore
import com.fifo.match.network.NetworkResult
import com.fifo.match.ui.activity.completeProfile.CompleteProfileActivity
import com.fifo.match.ui.activity.completeProfile.CompleteProfileViewModel
import com.fifo.match.ui.activity.completeProfile.adapter.ImageAdapter
import com.fifo.match.ui.base.BaseFragment
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ProfileSecondFragment :
    BaseFragment<CompleteProfileViewModel, FragmentProfileSecoundBinding>(CompleteProfileViewModel::class.java) {

    @Inject
    lateinit var myDataStore: MyDataStore

    private val viewModel: CompleteProfileViewModel by viewModels()

    private var profileUri: File? = null
    private lateinit var adapter: ImageAdapter
    private val listUri = arrayListOf<File?>(null, null, null, null, null, null, null, null, null)
    private var mSelectedPos = 0

    companion object {
        private const val PROFILE_PIC_REQUEST_CODE = 109
        private const val RECYCLER_VIEW_PIC_REQUEST_CODE = 209
        fun newInstance() = ProfileSecondFragment()
    }

    override fun getLayoutRes() = R.layout.fragment_profile_secound

    override fun onRender() {
        super.onRender()
        binding.activity = requireActivity()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        dataObservers()
        adapter = ImageAdapter(listUri,object : ImageAdapter.GetClick {
            override fun getClick(int: Int) {

            }
        })
        binding.rvImage.layoutManager = GridLayoutManager(requireActivity(), 3)
        binding.rvImage.adapter = adapter
        binding.rvImage.affectOnItemClicks({ position, view ->
            mSelectedPos = position
            ImagePicker.with(this).crop().compress(1024).maxResultSize(1080, 1080).start(RECYCLER_VIEW_PIC_REQUEST_CODE)
        })

        binding.ivPickImage.setOnClickListener {
            ImagePicker.with(this).crop().compress(1024).maxResultSize(1080, 1080).start(PROFILE_PIC_REQUEST_CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data!!
            when (requestCode) {
                PROFILE_PIC_REQUEST_CODE -> {
                    profileUri = uri.toFile()
                    binding.ivImage.setLocalImage(uri)
                }
                RECYCLER_VIEW_PIC_REQUEST_CODE -> {
                    listUri[mSelectedPos] = uri.toFile()
                    adapter.notifyDataSetChanged()
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    fun getPhotos() {
        val photos = adapter.getPhotos() as ArrayList<File>
        if (profileUri != null || photos.isNotEmpty()) {
            if (profileUri != null) {
                photos.add(profileUri!!)
                if (photos.size >= 4) {
                viewModel.getProfilePhotos(photos)
                } else {
                    requireActivity().toastInfo("please Upload three additional images")
                }
            } else {
                requireActivity().toastError("please Upload Profile Photo")
            }
        } else {
            requireActivity().toastError("Please upload a profile picture and three additional images")
        }
    }

    private fun dataObservers() {
        viewModel.responsePhotos.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(activity)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    if (response.data?.status == 200) {
                        (activity as CompleteProfileActivity).redirectToMembership()
                    } else {
                        toast(response.data?.message.toString())
                    }
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.userData.value?.profileComplete = 2
                        myDataStore.updateUser(viewModel.userData.value!!)
                    }
                }
                is NetworkResult.Error -> {
                    ShowProgressDialog.hideProgress()
                    requireActivity().toastError(response.exception.localizedMessage!!.toString())
                }
            }
        }
    }

}