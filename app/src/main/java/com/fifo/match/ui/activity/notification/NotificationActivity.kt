package com.fifo.match.ui.activity.notification

import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fifo.match.R
import com.fifo.match.databinding.ActivityNotificationBinding
import com.fifo.match.model.NotificationModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.utils.Constants
import com.fifo.match.ui.activity.home.adapter.NotificationAdapter
import com.fifo.match.ui.activity.match.MatchActivity
import com.fifo.match.ui.activity.view.ViewActivity
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : BaseActivity<NotificationViewModel, ActivityNotificationBinding>(NotificationViewModel::class.java) {

    private var listNotification = arrayListOf<NotificationModel>()
    private var adapter: NotificationAdapter? = null
    private val viewModel by viewModels<NotificationViewModel>()
    override fun getLayoutRes() = R.layout.activity_notification

    override fun initView() {
        transparentStatusBar()
        binding.viewModel = viewModel
        binding.activity = this

        viewModel.callNotificationApi()
        registerObservers()
        clearNotificationObservers()

        binding.tv2.setOnClickListener {
            viewModel.callClearNotificationApi()
        }

    }

    fun setAdapter() {
        adapter = NotificationAdapter(listNotification, object : NotificationAdapter.GetOnClick {
            override fun onLikeType(type: String, id: Int) {
                if (type == "like") {
                    openActivity<ViewActivity>(Constants.PROFILE_FLAG to Constants.PROFILE_FLAG, Constants.PROFILE to id.toString())
                }
            }
            override fun onMatchType(type: String, openId: Int) {
                if (type == "match") {
                    openActivity<MatchActivity>(Constants.LIKE_FLAG to openId.toString())
                }
            }
        })
        binding.rvNotification.adapter = adapter

    }

    private fun registerObservers() {
        viewModel.response.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                   ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<ArrayList<NotificationModel>>(it) {
                            listNotification = this
                            if (listNotification.isNotEmpty()) {
                                binding.rlNotifi.visible()
                                binding.tvNoFound.gone()
                            } else {
                                binding.rlNotifi.gone()
                                binding.tvNoFound.visible()
                            }
                            Log.d("TAGxxx", listNotification.toString())
                            setAdapter()
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

    private fun clearNotificationObservers() {
        viewModel.responseClearNotification.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    binding.tv2.snackx(response.data?.message.toString(),1500){
                        viewModel.callNotificationApi()
                    }
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