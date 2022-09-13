package com.fifo.match.ui.activity.view.fragment

import android.annotation.SuppressLint
import androidx.fragment.app.activityViewModels
import com.fifo.match.R
import com.fifo.match.databinding.FragmentInfoBinding
import com.fifo.match.ui.activity.view.ViewViewModel
import com.fifo.match.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_info.*

@AndroidEntryPoint
class InfoFragment : BaseFragment<ViewViewModel, FragmentInfoBinding>(ViewViewModel::class.java) {

    private val viewModel: ViewViewModel by activityViewModels()
    override fun getLayoutRes() = R.layout.fragment_info

    override fun onRender() {
        super.onRender()
        infoObservers()
    }

    @SuppressLint("SetTextI18n")
    private fun infoObservers(){
        viewModel.stSetData.observe(requireActivity()) {
            binding.apply {
                it?.let {
                    tvBusiness.text = it.userDetails!!.occupation!!.name
                    tvRelaship.text = it.userDetails.relationshipData!!.name
                    if (it.userDetails.questionnaire != null){
                    tvBodyType.text = it.userDetails.questionnaire.bodyType?:"_"
                    tvHeight.text = it.userDetails.questionnaire.height + " cm"
                    }
                    if (it.userDetails.questionnaire?.seeking != null){
                        tvSeeking.text = it.userDetails.questionnaire.seeking.toString()
                    }else{
                        tvSeeking.text = "_"
                    }
                    tvFIFO.text = it.userDetails.workingFifo
                    tveducation.text = it.userDetails.education!!.name
                    tvEti.text = it.userDetails.countryName
                    tvSwing.text = it.userDetails.swing?:"_"
                }

            }
        }
    }

}