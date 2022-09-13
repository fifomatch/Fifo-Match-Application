package com.fifo.match.ui.activity.view.fragment

import androidx.fragment.app.activityViewModels
import com.fifo.match.R
import com.fifo.match.databinding.FragmentSurveyAnswersBinding
import com.fifo.match.model.UserDetailsModel
import com.fifo.match.ui.activity.view.ViewViewModel
import com.fifo.match.ui.activity.view.adapter.AppreciateQualitiesAdapter
import com.fifo.match.ui.activity.view.adapter.TopQualitiesAdapter
import com.fifo.match.ui.base.BaseFragment
import com.fifo.match.utils.extensions.gone
import com.fifo.match.utils.extensions.visible
import com.google.android.flexbox.FlexboxLayoutManager


class SurveyAnswersFragment :
    BaseFragment<ViewViewModel, FragmentSurveyAnswersBinding>(ViewViewModel::class.java) {

    private var quality: UserDetailsModel.UserDetails.Questionnaire? = null

    private val viewModel: ViewViewModel by activityViewModels()
    override fun getLayoutRes() = R.layout.fragment_survey_answers

    override fun onRender() {
        super.onRender()
        infoObservers()
    }

    private fun infoObservers() {
        viewModel.stSetData.observe(requireActivity()) {
            binding.apply {

                if (it.userDetails?.questionnaire?.personalityTypes.toString().isNotEmpty()) {
                    tvAppre.text = it.userDetails?.questionnaire?.personalityTypes
                    tvAppre.visible()
                } else {
                    tvAppre.gone()
                }
                if (it.userDetails?.questionnaire?.kidsInFuture != null) {
                    tvKidsFuture.visible()
                    tvKidsFuture.text = it.userDetails.questionnaire.kidsInFuture
                } else tvKidsFuture.gone()

                if (it.userDetails?.questionnaire?.kids != null) {
                    tvKids.text = it.userDetails.questionnaire.kids
                    tvKids.visible()
                } else tvKids.gone()

                it?.userDetails.let {
                    quality = it?.questionnaire
                    setAdapter()
                }
            }
        }
    }

    private fun setAdapter() {
        if (quality?.myQualities != null) {
            val adapter = TopQualitiesAdapter(quality?.myQualities?.trim()?.split(",")!!)
            binding.rvQuality.layoutManager = FlexboxLayoutManager(requireContext())
            binding.rvQuality.adapter = adapter
        }

        if (quality?.qualitiesAppreciate != null) {
            val adapterApprecate =
                AppreciateQualitiesAdapter(quality?.qualitiesAppreciate?.trim()?.split(",")!!)
            binding.rvAppreciate.layoutManager = FlexboxLayoutManager(requireContext())
            binding.rvAppreciate.adapter = adapterApprecate
        }
    }

}