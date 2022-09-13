package com.fifo.match.ui.activity.appearance

import androidx.activity.viewModels
import com.fifo.match.R
import com.fifo.match.databinding.ActivityAppearanceBinding
import com.fifo.match.model.AppearanceModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.ui.activity.home.fragment.profile.ProfileViewModel
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.toastError
import com.fifo.match.utils.extensions.transparentStatusBar
import com.fifo.match.utils.extensions.tryCast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppearanceActivity : BaseActivity<ProfileViewModel, ActivityAppearanceBinding>(ProfileViewModel::class.java) {

    override fun getLayoutRes() = R.layout.activity_appearance
    private val viewModel by viewModels<ProfileViewModel>()

    override fun initView() {
        transparentStatusBar()
        binding.activity = this
        callAPI()

    }

   private fun callAPI(){
        viewModel.callAppearanceAPI()
        getObservers()
    }

    private fun getObservers() {
        viewModel.responseAppearance.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<AppearanceModel>(it) {
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

    private fun setData(data: AppearanceModel) {
        binding.apply {
            data.let {
                tvBodyType.text = it.questionnaire?.bodyType?:"_"
                tvheight.text = it.questionnaire?.height?:"_" + " " + it.questionnaire?.heightType?:"_"
                tvCountry.text = data.countryName?:"_"
                tvFifo.text = data.workingFifo?:"_"
                tvSwing.text = data.swing?:"_"
            }
        }

    }


}