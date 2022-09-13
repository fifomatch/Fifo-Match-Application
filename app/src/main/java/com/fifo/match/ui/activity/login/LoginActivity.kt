package com.fifo.match.ui.activity.login

import androidx.activity.viewModels
import com.fifo.match.R
import com.fifo.match.databinding.ActivityLoginBinding
import com.fifo.match.model.OtpModel
import com.fifo.match.ui.activity.otp.OtpActivity
import com.fifo.match.ui.activity.signup.SignupActivity
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.utils.Constants
import com.fifo.match.network.utils.Constants.CHECK_FLAG
import com.fifo.match.network.utils.Constants.Mobile_LOGIN
import com.fifo.match.network.utils.Constants.countryCode
import com.fifo.match.utils.CardView
import com.fifo.match.utils.GpsTracker
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>(LoginViewModel::class.java) {

    private var gpsTracker: GpsTracker? = null
    override fun getLayoutRes() = R.layout.activity_login
    private val mViewModel by viewModels<LoginViewModel>()

    override fun initView() {
        setStatusBarTransparent("#FFFFFFFF", true)
        binding.viewModel = mViewModel
        binding.activity = this
        initListeners()
        registerObservers()
//        binding.ccp.setCountryForNameCode(getDefaultCountryCode(this))
    }

    private fun initListeners() {
        initRoundView()
        getLocation()
        mViewModel.stCountryCode.set(binding.ccp.selectedCountryCode)
        binding.apply {
            ccp.setOnCountryChangeListener {
                mViewModel.stCountryCode.set(ccp.selectedCountryCode)
            }
            tvSignup.setOnClickListener {
                openActivity<SignupActivity>()
                finish()
            }
        }
    }

    private fun initRoundView() {
        binding.linearRoot.roundBorderedViewFromResId(15, R.color.white, R.color.gray, 1)
        binding.llOne.background = (CardView(
            resources.getColor(R.color.white), floatArrayOf(
                0f, 0f, 0f, 0f, 80f, 80f, 80f, 80f
            )
        ))
    }

    var otpx = ""
    private fun registerObservers() {
        mViewModel.response.observe(this@LoginActivity) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<OtpModel>(it){
                            /*otpx = otp.toString()*/
                        }
                    }
                    openActivity<OtpActivity>(Mobile_LOGIN to mViewModel.stMobileNumber.get().toString(),
                        CHECK_FLAG to "1",
                        countryCode to  mViewModel.stCountryCode.get().toString(),
                       /* Constants.OTP_ to otpx,*/)
                }
                is NetworkResult.Error -> {
                   ShowProgressDialog.hideProgress()
                    toastError(response.exception.localizedMessage!!.toString())

                }

            }
        }
    }

    private fun getLocation() {
        gpsTracker = GpsTracker(this)
        if (gpsTracker!!.canGetLocation()) {
        } else {
            gpsTracker!!.showSettingsAlert()
        }
    }

}

