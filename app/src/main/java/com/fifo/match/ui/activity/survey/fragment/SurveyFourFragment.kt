package com.fifo.match.ui.activity.survey.fragment

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.fifo.match.R
import com.fifo.match.databinding.FragmentSurveyFourBinding
import com.fifo.match.ui.activity.survey.SurveyViewModel
import com.fifo.match.ui.activity.survey.adapter.SeekingAdapter
import com.fifo.match.ui.base.BaseFragment
import com.fifo.match.utils.extensions.affectOnItemClicks
import com.fifo.match.utils.extensions.toastError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SurveyFourFragment : BaseFragment<SurveyViewModel, FragmentSurveyFourBinding>(SurveyViewModel::class.java) {

    val listSeeking = arrayListOf("Dating","Relationship","Friends","Friends with benefits")
    private  val adapter =  SeekingAdapter(listSeeking)

    private val viewModel: SurveyViewModel by activityViewModels()
    override fun getLayoutRes() = (R.layout.fragment_survey_four)

    override fun onRender() {
        super.onRender()
        surveyObservers()
        binding.rvSeeking.layoutManager = GridLayoutManager(requireContext(),2)
        binding.rvSeeking.adapter = adapter
        binding.rvSeeking.affectOnItemClicks { position, view ->
            viewModel.stSeeking.set(listSeeking[position])
        }
    }

    fun getSurveyFour(){
        if (viewModel.stSeeking.get().isNullOrEmpty()){
            requireActivity().toastError("Please select,what are you seeking")
        }else{
            viewModel.performIndex.set(4)
            viewModel.callSurveyFourApi()
        }
    }

    private fun surveyObservers(){
        viewModel.stSetData.observe(requireActivity()){
            if (it.seeking != null){
                viewModel.stSeeking.set(it.seeking.toString())
             adapter.setSelectedData(it.seeking!!.toString())
            }
        }
    }

}