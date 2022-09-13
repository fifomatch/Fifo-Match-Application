package com.fifo.match.ui.activity.viewYourProfile

import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.fifo.match.R
import com.fifo.match.databinding.ActivityViewYourProfileBinding
import com.fifo.match.model.LikeSavedModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.utils.Constants
import com.fifo.match.ui.activity.home.fragment.profile.ProfileViewModel
import com.fifo.match.ui.activity.view.ViewActivity
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewYourProfileActivity : BaseActivity<ProfileViewModel, ActivityViewYourProfileBinding>(ProfileViewModel::class.java) {

    private var  listView = arrayListOf<LikeSavedModel.Data>()
    private var dataModel : LikeSavedModel? = null

    override fun getLayoutRes() = R.layout.activity_view_your_profile
    private val viewModel by viewModels<ProfileViewModel>()

    override fun initView() {
        transparentStatusBar()
        binding.activity = this

        viewModel.stStatus.set("view")
        callApi()
    }

    private fun callApi(){
        viewModel.callHiddenProfileAPI()
        getObserver()
    }

    private fun setAdapter(){
        val adapter = ViewYourProfileAdapter(listView,object : ViewYourProfileAdapter.GetClick {
            override fun onProfileClick(id: Int, position: Int) {
                openActivity<ViewActivity>(Constants.PROFILE_FLAG to Constants.PROFILE_FLAG, Constants.PROFILE to id.toString())
            }
        })
        binding.rvView.layoutManager = GridLayoutManager(this, 2)
        binding.rvView.adapter = adapter
    }

    private fun getObserver() {
        viewModel.responseHideProfile.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<LikeSavedModel>(it){
                            dataModel = this
                            listView.addAll(dataModel!!.data)
                            if (listView.isNotEmpty()){
                                binding.rvView.visible()
                                binding.tvNotFound.gone()
                                binding.tvCountx.visible()
                                binding.tvCountx.text = this.count.toString() + " people viewed your profile"
                                setAdapter()
                            }else{
                                binding.tvCountx.gone()
                                binding.rvView.gone()
                                binding.tvNotFound.visible()
                            }
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