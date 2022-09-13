package com.fifo.match.ui.activity.match

import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.fifo.match.App
import com.fifo.match.R
import com.fifo.match.ui.activity.chat.ChatStartActivity
import com.fifo.match.databinding.ActivityMatchBinding
import com.fifo.match.model.CreateChatModel
import com.fifo.match.model.MatchModal
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.utils.Constants
import com.fifo.match.ui.activity.home.HomeActivity
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import com.github.hariprasanths.bounceview.BounceView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchActivity : BaseActivity<MatchViewModel, ActivityMatchBinding>(MatchViewModel::class.java) {

    override fun getLayoutRes() = R.layout.activity_match
    private val viewModel by viewModels<MatchViewModel>()

    override fun initView() {
        transparentStatusBar()
        binding.activity = this
        BounceView.addAnimTo(binding.btnHello)
        binding.btnHello.roundBorderedViewFromResId(15, R.color.orange, R.color.orange, 1)
        if (intent.hasExtra(Constants.LIKE_FLAG)) {
            viewModel.stId.set(intent.getStringExtra(Constants.LIKE_FLAG))
        }
        viewModel.callAPI()
        getObserver()
        getCreateChatObserver()

        binding.tvHome.setOnClickListener {
            openActivity<HomeActivity>()
        }

        binding.btnHello.setOnClickListener {
            viewModel.callCreateChat()
        }
    }

    private fun getObserver() {
        viewModel.response.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                   ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<MatchModal>(it) {
                            setData(this)
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

    private fun setData(data: MatchModal) {
        data.let {
            if (userData?.id.toString() == data.senderId.toString()) {
                viewModel.stUserID.set(data.receiverId.toString())
            }else{
            viewModel.stUserID.set(data.senderId.toString())
            }

            Glide.with(this).load(data.receiverPhoto?.name).into(binding.ivMale)
            Glide.with(this).load(data.senderPhoto?.name).into(binding.ivFemale)
        }
    }

    private  fun getCreateChatObserver(){
        viewModel.responseCreateChat.observe(this){ response ->
            when(response){
                is NetworkResult.Loading ->{
                   ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<CreateChatModel>(response.data.result){
                            val value = this.node
                            openActivity<ChatStartActivity>("groupID" to value.toString(), "isGroup" to "0")
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
}