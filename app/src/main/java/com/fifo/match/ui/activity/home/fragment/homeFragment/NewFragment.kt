package com.fifo.match.ui.activity.home.fragment.homeFragment

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.fifo.match.R
import com.fifo.match.`interface`.GetOnClick
import com.fifo.match.databinding.FragmentNewBinding
import com.fifo.match.model.MatchListModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.utils.Constants
import com.fifo.match.ui.activity.home.adapter.MatchesAdapter
import com.fifo.match.ui.activity.view.ViewActivity
import com.fifo.match.ui.base.BaseFragment
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.gone
import com.fifo.match.utils.extensions.openActivity
import com.fifo.match.utils.extensions.toastError
import com.fifo.match.utils.extensions.visible
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.notify

@AndroidEntryPoint
class NewFragment : BaseFragment<HomeFragViewModel, FragmentNewBinding>(HomeFragViewModel::class.java) {

    private val listNew = arrayListOf<MatchListModel.User>()
    private var adapter: MatchesAdapter? = null

    private val viewModel: HomeFragViewModel by activityViewModels()
    override fun getLayoutRes() = R.layout.fragment_new

    override fun onRender() {
        super.onRender()
        callApi()
    }

    private fun callApi() {
        onlineObservers()
        getLikeObserver()
    }

    private fun onlineObservers() {
        viewModel.stSetData.observe(requireActivity()) {
            listNew.clear()
            listNew.addAll(it.newUsers)
            if (listNew.isNullOrEmpty()) {
                binding.rvOnline.gone()
                binding.tvNoFound.visible()
            } else {
                binding.rvOnline.visible()
                binding.tvNoFound.gone()
            }
            if (isAdded) {
                setAdapter()
            }
        }
    }

    private fun setAdapter() {
         adapter = MatchesAdapter(listNew, object : GetOnClick {
            override fun onProfileClick(profileId: Int, position:Int) {
                viewModel.stUserID.set(profileId.toString())
                requireActivity().openActivity<ViewActivity>(
                    Constants.PROFILE_FLAG to Constants.PROFILE_FLAG
                    , Constants.PROFILE to profileId.toString())
            }

            override fun onCancelClick(cancelId: Int, position:Int) {
                viewModel.stStatus.set("cancel")
                viewModel.stUserID.set(cancelId.toString())
                viewModel.callSaveLikeCancel()
                listNew.removeAt(position)
                binding.rvOnline.adapter?.notifyItemRemoved(position)
            }

            override fun onLikeClick(likeId: Int, position:Int) {
                viewModel.stUserID.set(likeId.toString())
                viewModel.callUserLike()
                listNew.removeAt(position)
                binding.rvOnline.adapter?.notifyItemRemoved(position)
            }

            override fun onSaveClick(saveId: Int, position:Int) {
                viewModel.stStatus.set("saved")
                viewModel.stUserID.set(saveId.toString())
                viewModel.callSaveLikeCancel()
                listNew.removeAt(position)
                binding.rvOnline.adapter?.notifyItemRemoved(position)
            }
        })
        binding.rvOnline.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.rvOnline.adapter = adapter
        adapter?.notifyDataSetChanged()
    }

    private fun getLikeObserver() {
        viewModel.responseLike.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(activity)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
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