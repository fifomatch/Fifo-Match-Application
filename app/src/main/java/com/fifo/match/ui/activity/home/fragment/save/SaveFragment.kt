package com.fifo.match.ui.activity.home.fragment.save

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.fifo.match.R
import com.fifo.match.databinding.FragmentSaveBinding
import com.fifo.match.model.LikeSavedModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.utils.Constants
import com.fifo.match.ui.activity.home.adapter.SaveAdapter
import com.fifo.match.ui.activity.view.ViewActivity
import com.fifo.match.ui.base.BaseFragment
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaveFragment : BaseFragment<SaveViewModel, FragmentSaveBinding>(SaveViewModel::class.java) {

    private var  listSave = arrayListOf<LikeSavedModel.Data>()
    private var dataModel : LikeSavedModel? = null
    var counting = 0

    private val viewModel: SaveViewModel by viewModels()
    override fun getLayoutRes() = R.layout.fragment_save

    override fun onRender() {
        super.onRender()
        requireActivity().transparentStatusBar()
        viewModel.stStatus.set("saved")
        callApi()
        getRemoveObserver()
    }

    private fun callApi(){
        viewModel.callAPI()
        getObserver()
    }

    override fun onResume() {
        super.onResume()
        listSave.clear()
        viewModel.callAPI()
    }

    private fun setAdapter(){
        val adapter = SaveAdapter(listSave,object : SaveAdapter.GetClick {
            @SuppressLint("SetTextI18n")
            override fun onSavedClick(id: Int, position: Int) {
                counting = counting.minus(1)
                binding.tvCount.text =  "You have $counting profile saved"
                viewModel.stUserID.set(id.toString())
                viewModel.callRemoveUser()
                listSave.removeAt(position)
                binding.rvSaved.adapter?.notifyItemRemoved(position)
            }
            override fun onProfileClick(id: Int, position: Int) {
                requireActivity().openActivity<ViewActivity>(Constants.PROFILE_FLAG to Constants.PROFILE_FLAG, Constants.PROFILE to id.toString())
            }
        })
        binding.rvSaved.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.rvSaved.adapter = adapter
    }

    private fun getObserver() {
        viewModel.response.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(activity)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<LikeSavedModel>(it){
                            dataModel = this
                            listSave.addAll(dataModel!!.data)
                            setAdapter()
                            if (listSave.isNotEmpty()){
                                binding.rvSaved.visible()
                                binding.tvNotFound.gone()
                                binding.tvCount.visible()
                                viewModel.stCount.set(this.count.toString())
                                counting  = this.count!!
                            binding.tvCount.text = "You have " + counting.toString() + " profile saved"
                            }else{
                                binding.tvCount.gone()
                                binding.rvSaved.gone()
                                binding.tvNotFound.visible()
                            }

                        }
                    }
                }
                is NetworkResult.Error -> {
                   ShowProgressDialog.hideProgress()
                }
            }
        }
    }

    private fun getRemoveObserver() {
        viewModel.responseSave.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(activity)
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
//                    requireActivity().toastError(response.exception.localizedMessage!!.toString())
                }
            }
        }
    }


}