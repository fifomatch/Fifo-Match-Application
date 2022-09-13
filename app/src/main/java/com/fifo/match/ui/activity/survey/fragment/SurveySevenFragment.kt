package com.fifo.match.ui.activity.survey.fragment

import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.fifo.match.R
import com.fifo.match.databinding.FragmentSurveySevenBinding
import com.fifo.match.ui.activity.survey.SurveyViewModel
import com.fifo.match.ui.base.BaseFragment
import com.fifo.match.utils.extensions.getDrawablexx
import com.fifo.match.utils.extensions.toastError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SurveySevenFragment : BaseFragment<SurveyViewModel, FragmentSurveySevenBinding>(SurveyViewModel::class.java) {

    private val viewModel: SurveyViewModel by activityViewModels()
    override fun getLayoutRes() = (R.layout.fragment_survey_seven)

    override fun onRender() {
        super.onRender()
        binding.fragment = this
        surveyObservers()
    }

    fun onType(int: Int) {
        when (int) {
            1 -> {
                viewModel.stPersonalityTypes.set(getString(R.string.extrovert))
                binding.btnKidsYes.background = requireActivity().getDrawablexx(R.drawable.shape_select)
                binding.btnKidsNo.background = requireActivity().getDrawablexx(R.drawable.shape_unselect)
                binding.btnKidsYes.setTextColor(ContextCompat.getColor(requireActivity(), R.color.orange))
                binding.btnKidsNo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray));
            }
            2 -> {
                viewModel.stPersonalityTypes.set(getString(R.string.introvert))
                binding.btnKidsNo.background = requireActivity().getDrawablexx(R.drawable.shape_select)
                binding.btnKidsYes.background = requireActivity().getDrawablexx(R.drawable.shape_unselect)
                binding.btnKidsNo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.orange));
                binding.btnKidsYes.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray));
            }
        }

    }

    fun getSurveySeven() {
        if (viewModel.stPersonalityTypes.get().isNullOrEmpty()) {
            requireActivity().toastError("Please select Personality type")
        } else {
            viewModel.callSurveySevenApi()
            viewModel.performIndex.set(7)
        }
    }

    private fun surveyObservers() {
        viewModel.stSetData.observe(requireActivity()) {
            if (it.personality != null) {
                if (it.personality == getString(R.string.extrovert)) {
                    onType(1)
                } else {
                    onType(2)
                }
            }
        }
    }

}