package com.fifo.match.ui.activity.view.fragment

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.fifo.match.R
import com.fifo.match.databinding.FragmentPhotoBinding
import com.fifo.match.model.UserDetailsModel
import com.fifo.match.ui.activity.membership.MembershipActivity
import com.fifo.match.ui.activity.upgradeMembership.UpgradeMembershipActivity
import com.fifo.match.ui.activity.view.ViewViewModel
import com.fifo.match.ui.activity.view.adapter.PhotoPremiumAdapter
import com.fifo.match.ui.base.BaseFragment
import com.fifo.match.utils.extensions.gone
import com.fifo.match.utils.extensions.openActivity
import com.fifo.match.utils.extensions.toast
import com.fifo.match.utils.extensions.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoFragment : BaseFragment<ViewViewModel, FragmentPhotoBinding>(ViewViewModel::class.java) {

    var isPremium = false
    var isPlanFemale = false
    private val listPhotos  = arrayListOf<UserDetailsModel.UserDetails.Photo?>()

    private val viewModel: ViewViewModel by activityViewModels()
    override fun getLayoutRes()=R.layout.fragment_photo

    override fun onRender() {
        super.onRender()
        planObservers()
        infoObservers()

        binding.clLayout.setOnClickListener {
            if (userData!!.gender.equals("Woman")){
                if (!isPremium){
                    requireActivity().openActivity<MembershipActivity>()
                }else{
                    requireActivity().openActivity<UpgradeMembershipActivity>()
                }
            }else{
                requireActivity().openActivity<UpgradeMembershipActivity>()
            }

        }
    }

    private fun infoObservers(){
        viewModel.stSetData.observe(requireActivity()) {
            binding.apply {
                listPhotos.addAll(it.userDetails!!.photos)
                setAdapter()
            }
        }
    }

    private fun setAdapter(){
        val adapter = PhotoPremiumAdapter(listPhotos,isPremium)
        binding.rvPhoto.layoutManager = GridLayoutManager(requireContext(),3)
        binding.rvPhoto.adapter = adapter
    }

    private fun planObservers(){
        viewModel.stCheckPlan.observe(this){ data->
                if (data.planId.equals("plan_id_01")){
                    binding.clLayout.visible()
                    binding.tvInfo.visible()
                    isPremium = false
                }else if (data.planId.equals("plan_id_02")) {
                    binding.clLayout.gone()
                    binding.tvInfo.gone()
                    isPremium = true
                }
        }
    }
}