package com.fifo.match.ui.activity.home.fragment

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.fifo.match.R
import com.fifo.match.databinding.FragmentLikeBinding
import com.fifo.match.model.CreateChatModel
import com.fifo.match.model.LikeNewModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.utils.Constants
import com.fifo.match.ui.activity.chat.ChatStartActivity
import com.fifo.match.ui.activity.home.adapter.LikeAdapter
import com.fifo.match.ui.activity.home.fragment.save.SaveViewModel
import com.fifo.match.ui.activity.view.ViewActivity
import com.fifo.match.ui.base.BaseFragment
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LikeFragment : BaseFragment<SaveViewModel, FragmentLikeBinding>(SaveViewModel::class.java) {

    private var listSave = arrayListOf<LikeNewModel>()

    private val viewModel: SaveViewModel by viewModels()
    override fun getLayoutRes() = R.layout.fragment_like

    override fun onRender() {
        super.onRender()
        viewModel.stStatus.set("like")
        callApi()
        getCreateChatObserver()
    }

    private fun callApi() {
        viewModel.callLikeListAPI()
        getObserver()
    }

    private fun setAdapter() {
        val adapter = LikeAdapter(listSave, object : LikeAdapter.GetClick {
            override fun onProfileClick(id: Int) {
                requireActivity().openActivity<ViewActivity>(
                    Constants.PROFILE_FLAG to Constants.PROFILE_FLAG,
                    Constants.PROFILE to id.toString()
                )
            }
            override fun onMessageClick(id: Int) {
                 viewModel.stUserID.set(id.toString())
                viewModel.callCreateChat()
            }
        })

        binding.rvSaved.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.rvSaved.adapter = adapter
    }

    private fun getObserver() {
        viewModel.responseLikeList.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(activity)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<ArrayList<LikeNewModel>>(it) {
                            listSave = this
                            setAdapter()
                            if (listSave.isNotEmpty()) {
                                Log.d("TAGlisting", listSave.toString())
                                binding.rvSaved.visible()
                                binding.tvNotFound.gone()
                            } else {
                                binding.rvSaved.gone()
                                binding.tvNotFound.visible()
                            }
                        }
                    }
                }

                is NetworkResult.Error -> {
                   ShowProgressDialog.hideProgress()
                   // requireActivity().toastError(response.exception.localizedMessage!!.toString())
                }
            }
        }
    }

    private  fun getCreateChatObserver(){
        viewModel.responseCreateChat.observe(this){ response ->
            when(response){
                is NetworkResult.Loading ->{
                    ShowProgressDialog.showProgress(activity)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<CreateChatModel>(response.data.result){
                            val value = this.node
                            activity?.openActivity<ChatStartActivity>("groupID" to value.toString(), "isGroup" to "0")
                        }
                    }
                }
                is NetworkResult.Error -> {
                   ShowProgressDialog.hideProgress()

                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        callApi()
    }
}