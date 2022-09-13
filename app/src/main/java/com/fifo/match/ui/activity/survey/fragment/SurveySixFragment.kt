package com.fifo.match.ui.activity.survey.fragment

import androidx.fragment.app.activityViewModels
import com.fifo.match.R
import com.fifo.match.databinding.FragmentSurveySixBinding
import com.fifo.match.ui.activity.survey.SurveyViewModel
import com.fifo.match.ui.activity.survey.adapter.QualitiesTypeAdapter
import com.fifo.match.ui.base.BaseFragment
import com.fifo.match.utils.extensions.toastError
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SurveySixFragment : BaseFragment<SurveyViewModel, FragmentSurveySixBinding>(SurveyViewModel::class.java) {

    val list = arrayListOf(
        "You are his/her priority",
        "Laugh Together",
        "Supports/Takes care of you",
        "Meaningful conversation",
        "Full of surprises/ Fascinates you",
        "Honest/Loyal",
        "Cute/Dear",
        "Quality Sex"
    )

    private val adapter = QualitiesTypeAdapter(list)
    private val viewModel: SurveyViewModel by activityViewModels()
    override fun getLayoutRes() = (R.layout.fragment_survey_six)

    override fun onRender() {
        super.onRender()
        surveyObservers()
        binding.rvAppreciate.layoutManager = FlexboxLayoutManager(requireActivity())
        binding.rvAppreciate.adapter = adapter

    }

    fun getSurveySix() {
        when {
            adapter.getSelectedValues().isNullOrEmpty() -> {
                requireActivity().toastError("Please select  Qualities")
            }
            /*adapter.getSelectedValues().size <= 3 -> {
                requireActivity().toastError("Please select 4 Qualities")
            }*/
            else -> {
                viewModel.callSurveySixApi(adapter.getSelectedValues())
                    viewModel.performIndex.set(6)
            }
        }
    }

    private fun surveyObservers() {
        viewModel.stSetData.observe(requireActivity()) {
            if (it.qualitiesAppreciate != null) {
                val em = it.qualitiesAppreciate!!.split(",")
                val selectedPositions = hashSetOf<String>()
                em.forEach {
                    selectedPositions.add(it.trim())
                }
                adapter.updateSelectedValues(selectedPositions)
            }
        }
    }

}