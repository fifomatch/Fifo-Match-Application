package com.fifo.match.ui.activity.survey.fragment

import androidx.fragment.app.activityViewModels
import com.fifo.match.R
import com.fifo.match.databinding.FragmentSurveySecoundBinding
import com.fifo.match.ui.activity.survey.SurveyViewModel
import com.fifo.match.ui.activity.survey.adapter.BodyTypeAdapter
import com.fifo.match.ui.base.BaseFragment
import com.fifo.match.utils.extensions.affectOnItemClicks
import com.fifo.match.utils.extensions.toastError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SurveySecondFragment :
    BaseFragment<SurveyViewModel, FragmentSurveySecoundBinding>(SurveyViewModel::class.java) {

    private val viewModel: SurveyViewModel by activityViewModels()

    var listBody = arrayListOf("Slim", "Athletic", "A few pounds")
    private  val adapter =  BodyTypeAdapter(listBody)
    companion object {
        fun newInstance() = SurveySecondFragment()
    }

    override fun getLayoutRes() = (R.layout.fragment_survey_secound)

    override fun onRender() {
        super.onRender()
        surveyObservers()
        binding.rvtype.adapter = adapter
        binding.rvtype.affectOnItemClicks { position, view ->
            viewModel.stBodyType.set(listBody[position])
        }
    }


    fun getSurveySecond() {
        if (viewModel.stBodyType.get().isNullOrEmpty()) {
            requireActivity().toastError("Please select body type")
        } else {
            viewModel.performIndex.set(2)
            viewModel.callSurveySecondApi()
        }
    }

    private fun surveyObservers(){
        viewModel.stSetData.observe(requireActivity()){
            it?.let { data->
                if (data.bodyType != null){
                    viewModel.stBodyType.set(data.bodyType)
                    adapter.setSelectedData(data.bodyType!!.toString())
                }

            }

        }
    }

}