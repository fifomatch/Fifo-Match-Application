package com.fifo.match.ui.activity.survey.fragment

import androidx.fragment.app.activityViewModels
import com.fifo.match.R
import com.fifo.match.databinding.FragmentSurveyFiveBinding
import com.fifo.match.ui.activity.survey.SurveyViewModel
import com.fifo.match.ui.activity.survey.adapter.QualitiesAdapter
import com.fifo.match.ui.base.BaseFragment
import com.fifo.match.utils.extensions.toastError
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SurveyFiveFragment : BaseFragment<SurveyViewModel, FragmentSurveyFiveBinding>(SurveyViewModel::class.java) {

    val list = arrayListOf("Smart", "Cute/Dear", "Honest/ Loyal",
        "Careerist",
        "Sense of humor",
        "Energy", "Supportive/Helpful", "Spontaneous"
    )

    private  val adapter =  QualitiesAdapter(list)
    private val viewModel: SurveyViewModel by activityViewModels()
    override fun getLayoutRes() = (R.layout.fragment_survey_five)

    override fun onRender() {
        super.onRender()
        surveyObservers()
        binding.rvQualities.layoutManager = FlexboxLayoutManager(requireActivity())
        binding.rvQualities.adapter = adapter
    }


    fun getSurveyFive() {
        when {
            adapter.getSelectedValues().isNullOrEmpty() -> {
                requireActivity().toastError("Please select  Qualities")
            }
            /*adapter.getSelectedValues().size <= 3 -> {
                requireActivity().toastError("Please select 4 Qualities")
            }*/
            else -> {
                viewModel.callSurveyFiveApi(adapter.getSelectedValues())
                viewModel.performIndex.set(5)
            }
        }
    }

    private fun surveyObservers(){
        viewModel.stSetData.observe(requireActivity()){
            if (it.myQualities != null){
                val em = it.myQualities!!.split(",")
                val selectedPositions= hashSetOf<String>()
                em.forEach {
                    selectedPositions.add(it.trim())
                }
                adapter.updateSelectedValues(selectedPositions)
            }
        }
    }

}