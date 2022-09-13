package com.fifo.match.ui.activity.survey.fragment

import android.annotation.SuppressLint
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.fifo.match.R
import com.fifo.match.databinding.FragmentSurveyFirstBinding
import com.fifo.match.ui.activity.survey.SurveyViewModel
import com.fifo.match.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SurveyFirstFragment :
    BaseFragment<SurveyViewModel, FragmentSurveyFirstBinding>(SurveyViewModel::class.java) {

    private val viewModel: SurveyViewModel by activityViewModels()
    var valueData = 0

    companion object {
        fun newInstance() = SurveyFirstFragment()
    }

    override fun getLayoutRes() = (R.layout.fragment_survey_first)

    override fun onRender() {
        super.onRender()
        binding.fragment = requireActivity()
        initListeners()

    }

    override fun refresh() {
        super.refresh()
        registerObsever()
    }

    @SuppressLint("SetTextI18n")
    private fun initListeners() {
        viewModel.stHeightType.set("cm")
        viewModel.stHeightBar.set("121")
        binding.swSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                valueData = 120 + binding.mSeekBarHieght.progress
                viewModel.stHeightBar.set(valueData.toString())
                if (isChecked) {
                    viewModel.stHeightType.set("inches")
                    binding.txtValueHieght.text = cmToFeet(viewModel.stHeightBar.get()!!.toInt()) + "″"
                    binding.tvCM.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                    binding.tvInches.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                } else {
                    viewModel.stHeightType.set("cm")
                    binding.txtValueHieght.text = "" + valueData + "cm"
                    binding.tvInches.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                    binding.tvCM.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }
        }
        binding.mSeekBarHieght.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                valueData = 120 + progress
                viewModel.stHeightBar.set(valueData.toString())
                if (viewModel.stHeightType.get().equals("cm")) {
                    binding.txtValueHieght.text = "" + viewModel.stHeightBar.get() + "cm"
                } else {
                    binding.txtValueHieght.text =
                        cmToFeet(viewModel.stHeightBar.get()!!.toInt()) + "″"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

    }

    fun getSurveyFirst() {
        viewModel.callSurveyFirstApi()
        viewModel.performIndex.set(1)
    }


    private fun registerObsever() {
        viewModel.stSetData.observe(requireActivity()) { data->
            data?.let {
                if (it.heightType != null && it.height != null) {
                    if (it.heightType.toString() == "cm") {
                        binding.swSwitch.isChecked = false
                        binding.txtValueHieght.text = it.height.toString() + " cm"
                    } else {
                        binding.swSwitch.isChecked = true
                        val convert = cmToFeet(it.height) + "″"
                        binding.txtValueHieght.text = convert
                    }
                    binding.mSeekBarHieght.post {
                        binding.mSeekBarHieght.progress = it.height
                    }
                }
            }

        }
    }

    fun cmToFeet(centi: Int): String {
        val inch: Double = 0.3937 * centi
        val feet: Double = inch / 12
        val remainInch: Double = inch % 12
        return "".plus(feet.toInt()).plus("'").plus(remainInch.toInt())
    }
}

/*
@Throws(NumberFormatException::class)
private fun convertToInches( str: String) {
    val value: Double = str.toDouble()
    val inches = (value * 2.54).roundToInt().toInt()
    requireActivity().toastInfo(inches.toString() + "inches")
}

@Throws(NumberFormatException::class)
private fun convertToCentimeter( str: String) {
    val value: Double = str.toDouble()
    val cm = (value * 0.39).roundToInt().toInt()
    requireActivity().toastInfo(cm.toString() + "cm")
}*/
