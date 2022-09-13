package com.fifo.match.ui.activity.congratulations

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.fifo.match.R
import com.fifo.match.databinding.ActivityCongratulationsBinding
import com.fifo.match.local.MyDataStore
import com.fifo.match.model.CheckPlanModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.ui.activity.home.HomeActivity
import com.fifo.match.ui.activity.survey.SurveyActivity
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CongratulationsActivity : BaseActivity<CongratulationsViewModel, ActivityCongratulationsBinding>(CongratulationsViewModel::class.java) {

    @Inject
    lateinit var myDataStore: MyDataStore
    override fun getLayoutRes() = R.layout.activity_congratulations
    private val viewModel by viewModels<CongratulationsViewModel>()

    override fun initView() {
        transparentStatusBar()
        binding.viewModel = viewModel
        binding.activity = this
        binding.ripple.startRippleAnimation()
        binding.ripple1.startRippleAnimation()
        binding.ripple2.startRippleAnimation()


        binding.btnFill.roundBorderedViewFromResId(20, R.color.white, R.color.white, 1)
        binding.btnSkip.roundBorderedViewFromResId(20, R.color.white, R.color.white, 1)

        binding.btnFill.setOnClickListener {
            openActivity<SurveyActivity>()
        }

        viewModel.callActivePlan()
        planObserver()

        binding.btnSkip.setOnClickListener {
            callSkipApi()
        }

    }

    private fun callSkipApi(){
        viewModel.callSurveySevenApi()
        skipObserver()
    }

    private fun planObserver() {
        viewModel.responsePlan.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<CheckPlanModel>(it) {
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

    private fun setData(data: CheckPlanModel) {
        data.let {
            if (it.planId.equals("plan_id_01")) {
                binding.btnSkip.gone()
            } else if (it.planId.equals("plan_id_02")) {
                binding.btnSkip.visible()
            }
        }
    }

    private fun skipObserver() {
        viewModel.responseSurvey.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                   ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        openActivity<HomeActivity>()
                        lifecycleScope.launch(Dispatchers.IO) {
                            userData?.sureyStatus = 7
                            myDataStore.updateUser(userData!!)
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


}