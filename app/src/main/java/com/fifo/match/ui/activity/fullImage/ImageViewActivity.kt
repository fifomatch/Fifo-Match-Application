package com.fifo.match.ui.activity.fullImage

import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.fifo.match.R
import com.fifo.match.databinding.ActivityImageViewBinding
import com.fifo.match.network.utils.Constants
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.extensions.transparentStatusBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageViewActivity :
    BaseActivity<ImageViewModel, ActivityImageViewBinding>(ImageViewModel::class.java) {

    private val viewModel by viewModels<ImageViewModel>()
    override fun getLayoutRes() = R.layout.activity_image_view

    override fun initView() {
        transparentStatusBar()
        binding.activity = this

        if (intent.hasExtra(Constants.IMAGE_FLAG)){
            viewModel.stImageView.set(intent.getStringExtra(Constants.IMAGE_FLAG))
        }
        Glide.with(this).load(viewModel.stImageView.get()).into(binding.imageview)

    }

}