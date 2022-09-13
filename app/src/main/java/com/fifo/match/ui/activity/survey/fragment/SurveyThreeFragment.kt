package com.fifo.match.ui.activity.survey.fragment

import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.fifo.match.R
import com.fifo.match.databinding.FragmentSurveyThreeBinding
import com.fifo.match.ui.activity.survey.SurveyViewModel
import com.fifo.match.ui.base.BaseFragment
import com.fifo.match.utils.extensions.getDrawablexx
import com.fifo.match.utils.extensions.toastError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SurveyThreeFragment : BaseFragment<SurveyViewModel, FragmentSurveyThreeBinding>(SurveyViewModel::class.java) {

    private val viewModel: SurveyViewModel by activityViewModels()

    override fun getLayoutRes() = (R.layout.fragment_survey_three)

    override fun onRender() {
        super.onRender()
        binding.fragment = this
        surveyObservers()
    }

    fun getSurveyThree(){
        if (viewModel.stKids.get().isNullOrEmpty()){
            requireActivity().toastError("Please select, do you have kids")

        } else if (viewModel.stKidsInFuture.get().isNullOrEmpty()){
            requireActivity().toastError("Please select, do you want kids in the future")
        }else{
            viewModel.performIndex.set(3)
            viewModel.callSurveyThirdApi()
        }
    }


    fun onKidsSelects(int: Int){
        when(int){
            1->{
                viewModel.stKids.set(getString(R.string.yes))
                binding.btnKidsYes.background = requireActivity().getDrawablexx(R.drawable.shape_select)
                binding.btnKidsNo.background = requireActivity().getDrawablexx(R.drawable.shape_unselect)
                binding.btnKidsYes.setTextColor(ContextCompat.getColor(requireActivity(), R.color.orange))
                binding.btnKidsNo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray));
            }
            2->{
                viewModel.stKids.set(getString(R.string.no))
                binding.btnKidsNo.background = requireActivity().getDrawablexx(R.drawable.shape_select)
                binding.btnKidsYes.background = requireActivity().getDrawablexx(R.drawable.shape_unselect)
                binding.btnKidsNo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.orange));
                binding.btnKidsYes.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray));
            }
        }
    }

    fun onFutureKids(int: Int){
        when(int){
            1->{
                viewModel.stKidsInFuture.set(getString(R.string.yes))
                binding.btnFutureYes.background = requireActivity().getDrawablexx(R.drawable.shape_select)
                binding.btnFuturesNo.background = requireActivity().getDrawablexx(R.drawable.shape_unselect)
                binding.btnFuturesYesNo.background = requireActivity().getDrawablexx(R.drawable.shape_unselect)
                binding.btnFutureYes.setTextColor(ContextCompat.getColor(requireActivity(), R.color.orange));
                binding.btnFuturesNo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray));
                binding.btnFuturesYesNo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray));
            }
            2->{
                viewModel.stKidsInFuture.set(getString(R.string.no))
                binding.btnFuturesYesNo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray));
                binding.btnFuturesNo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.orange));
                binding.btnFuturesNo.background = requireActivity().getDrawablexx(R.drawable.shape_select)
                binding.btnFutureYes.background = requireActivity().getDrawablexx(R.drawable.shape_unselect)
                binding.btnFuturesYesNo.background = requireActivity().getDrawablexx(R.drawable.shape_unselect)
                binding.btnFutureYes.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray));
            }
            3->{
                viewModel.stKidsInFuture.set(getString(R.string.yes_but_not_in_the_near_future))
                binding.btnFuturesYesNo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.orange));
                binding.btnFutureYes.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray));
                binding.btnFuturesNo.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray));
                binding.btnFuturesYesNo.background = requireActivity().getDrawablexx(R.drawable.shape_select)
                binding.btnFuturesNo.background = requireActivity().getDrawablexx(R.drawable.shape_unselect)
                binding.btnFutureYes.background = requireActivity().getDrawablexx(R.drawable.shape_unselect)
            }

        }
    }

    private fun surveyObservers() {
        viewModel.stSetData.observe(requireActivity()){
            if (it.kids != null && it.kidsInFuture != null){
                if (viewModel.stSetData.value?.kids == "Yes"){
                    onKidsSelects(1)
                }else{
                    onKidsSelects(2)
                }
                when(it.kidsInFuture){
                    getString(R.string.yes)->{
                        onFutureKids(1)
                    }
                    getString(R.string.no)->{
                        onFutureKids(2)
                    }
                    getString(R.string.yes_but_not_in_the_near_future)->{
                        onFutureKids(3)
                    }
                }
            }
        }


    }

}