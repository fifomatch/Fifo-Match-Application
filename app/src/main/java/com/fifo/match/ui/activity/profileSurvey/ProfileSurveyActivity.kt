package com.fifo.match.ui.activity.profileSurvey

import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.fifo.match.R
import com.fifo.match.databinding.ActivityProfileSurveyBinding
import com.fifo.match.model.SurveyModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.ui.activity.survey.SurveyActivity
import com.fifo.match.ui.activity.survey.SurveyViewModel
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.openActivity
import com.fifo.match.utils.extensions.toastError
import com.fifo.match.utils.extensions.transparentStatusBar
import com.fifo.match.utils.extensions.tryCast
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class ProfileSurveyActivity : BaseActivity<SurveyViewModel, ActivityProfileSurveyBinding>(SurveyViewModel::class.java) {

    override fun getLayoutRes() = R.layout.activity_profile_survey
    private val viewModel by viewModels<SurveyViewModel>()

    override fun initView() {
        transparentStatusBar()
        binding.activity = this

        viewModel.getSurveyDataApi()
        getSurveyDataObserver()

        binding.ivEditSurvey.setOnClickListener {
            openActivity<SurveyActivity>()
        }

    }

    private fun getSurveyDataObserver() {
        viewModel.responseT.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<SurveyModel>(it) {
                            setData(this)
                        }
                    }
                }
                is NetworkResult.Error -> {
                   ShowProgressDialog.hideProgress()
                    toastError(response.exception.localizedMessage!!.toString())
                }
            }
        }
    }

    private fun setData(surveyModel: SurveyModel) {
        surveyModel.let {

            /** Height*/
            if (it.heightType != null && it.height != null) {
                if (it.heightType.toString() == "cm") {
                    binding.tvHeight.text = it.height.toString() + " cm"
                } else {
                    val convert = cmToFeet(it.height) + "″"
                    binding.tvHeight.text = "$convert inches"
                }
            }

            /** Body Type */
            if (it.bodyType.toString().isNotEmpty() && it.bodyType != null) {
                viewModel.stBodyType.set(it.bodyType)
                binding.tvBodyType.text = it.bodyType.toString()
            }

            /** Qualities */
            if (it.myQualities.toString().isNotEmpty()) {
                it.myQualities?.trim()?.split(",")?.forEach { myQuality ->
                    val textView = TextView(this)
                    textView.background = ContextCompat.getDrawable(this, R.drawable.shape_fix_survey)
                    textView.setTextColor(resources.getColor(R.color.black));
                    val typeface = ResourcesCompat.getFont(this, R.font.sf_pro_text_light)
                    textView.typeface = typeface
                    textView.setPadding(resources.getDimension(com.intuit.sdp.R.dimen._15sdp).roundToInt(), resources.getDimension(com.intuit.sdp.R.dimen._4sdp).roundToInt(), resources.getDimension(com.intuit.sdp.R.dimen._15sdp).roundToInt(), resources.getDimension(com.intuit.sdp.R.dimen._4sdp).roundToInt())
                    val flexboxLayout = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
                    val horizontalMargin = resources.getDimension(com.intuit.sdp.R.dimen._8sdp).roundToInt()
                    val verticalMargin = resources.getDimension(com.intuit.sdp.R.dimen._8sdp).roundToInt()
                    flexboxLayout.setMargins(horizontalMargin, verticalMargin, 0, 0)
                    textView.text = myQuality.trim()
                    textView.layoutParams = flexboxLayout
                    binding.flexBox.flexDirection = FlexDirection.ROW
                    binding.flexBox.flexWrap = FlexWrap.WRAP
                    binding.flexBox.addView(textView)
                }
            }

            /** Qualities Appreciate */

            if (it.qualitiesAppreciate != null){
                if (it.qualitiesAppreciate.isNotEmpty()) {
                    it.qualitiesAppreciate.trim().split(",").forEach { myQuality ->
                        val textView = TextView(this)
                        textView.background = ContextCompat.getDrawable(this, R.drawable.shape_fix_survey)
                        textView.setTextColor(resources.getColor(R.color.black));
                        val typeface = ResourcesCompat.getFont(this, R.font.sf_pro_text_light)
                        textView.typeface = typeface
                        textView.setPadding(
                            resources.getDimension(com.intuit.sdp.R.dimen._15sdp).roundToInt(),
                            resources.getDimension(com.intuit.sdp.R.dimen._4sdp).roundToInt(),
                            resources.getDimension(com.intuit.sdp.R.dimen._15sdp).roundToInt(),
                            resources.getDimension(com.intuit.sdp.R.dimen._4sdp).roundToInt()
                        )
                        val flexboxLayout = FlexboxLayout.LayoutParams(
                            FlexboxLayout.LayoutParams.WRAP_CONTENT,
                            FlexboxLayout.LayoutParams.WRAP_CONTENT
                        )
                        val horizontalMargin = resources.getDimension(com.intuit.sdp.R.dimen._8sdp).roundToInt()
                        val verticalMargin = resources.getDimension(com.intuit.sdp.R.dimen._8sdp).roundToInt()
                        flexboxLayout.setMargins(horizontalMargin, verticalMargin, 0, 0)
                        if (myQuality.isNotEmpty()){
                            textView.text = myQuality.trim()
                            textView.layoutParams = flexboxLayout
                            binding.flexBoxQualities.flexDirection = FlexDirection.ROW
                            binding.flexBoxQualities.flexWrap = FlexWrap.WRAP
                            binding.flexBoxQualities.addView(textView)
                        }

                    }
                }
            }

            /** Seeking */
            if (it.seeking.toString().isNotEmpty()) {
                binding.tvSeeking.text = it.seeking
            }

            /** kids */
            if (it.kids.toString().isNotEmpty()) {
                binding.tvKids.text = it.kids
            }

            /** kidsInFuture */
            if (it.kidsInFuture.toString().isNotEmpty()) {
                binding.tvKidsFuture.text = it.kidsInFuture
            }

            /** Personality Types */
            if (it.personalityTypes.toString().isNotEmpty()) {
                binding.tvperso.text = it.personalityTypes
            }

        }
    }

    private fun cmToFeet(centi: Int): String {
        val inch: Double = 0.3937 * centi
        val feet: Double = inch / 12
        val remainInch: Double = inch % 12
        return "".plus(feet.toInt()).plus("'").plus(remainInch.toInt())
    }

}