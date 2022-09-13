package com.fifo.match.ui.activity.hiddenProfile

import android.annotation.SuppressLint
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.fifo.match.R
import com.fifo.match.databinding.ActivityHiddenProfilesBinding
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
class HiddenProfilesActivity : BaseActivity<ProfileViewModel, ActivityHiddenProfilesBinding>(ProfileViewModel::class.java) {

    var counting = 0
    private var  listHidden = arrayListOf<LikeSavedModel.Data>()
    private var dataModel : LikeSavedModel? = null

    override fun getLayoutRes() = R.layout.activity_hidden_profiles
    private val viewModel by viewModels<ProfileViewModel>()

    override fun initView() {
        transparentStatusBar()
        binding.activity = this
        viewModel.stStatus.set("hide")
        callApi()
        getRemoveObserver()
    }

    private fun callApi(){
        viewModel.callHiddenProfileAPI()
        getObserver()
    }

    private fun setAdapter(){
        val adapter = HiddenAdapter(listHidden,object : HiddenAdapter.GetClick {
            @SuppressLint("SetTextI18n")
            override fun onButtonClick(id: Int, position: Int) {
                counting = counting.minus(1)
                binding.tvCount.text =  "You have $counting profile saved"
                viewModel.stUserID.set(id.toString())
                viewModel.callRemoveUser()
                listHidden.removeAt(position)
                binding.rvList.adapter?.notifyItemRemoved(position)
            }
            override fun onProfileClick(id: Int, position: Int) {
               openActivity<ViewActivity>(
                    Constants.PROFILE_FLAG to Constants.PROFILE_FLAG
                    , Constants.PROFILE to id.toString())
            }
        })
        binding.rvList.layoutManager = GridLayoutManager(this, 2)
        binding.rvList.adapter = adapter
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
                            setAdapter()
                            dataModel = this
                            listHidden.addAll(dataModel!!.data)
                            if (listHidden.isNotEmpty()){
                                binding.rvList.visible()
                                binding.tvNotFound.gone()
                                binding.tvCount.visible()
                                counting = this.count!!
                                binding.tvCount.text = "You have " +  this.count.toString() + " hidden profiles"
                            }else{
                                binding.tvCount.gone()
                                binding.rvList.gone()
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

    private fun getRemoveObserver() {
        viewModel.responseSave.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                   ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<LikeSavedModel>(it){

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

    override fun onResume() {
        super.onResume()
        listHidden.clear()
        viewModel.callHiddenProfileAPI()
    }

}