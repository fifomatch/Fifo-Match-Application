package com.fifo.match.ui.activity.signup

import android.graphics.Paint
import androidx.activity.viewModels
import com.fifo.match.R
import com.fifo.match.databinding.ActivitySignupBinding
import com.fifo.match.model.OtpModel
import com.fifo.match.ui.activity.login.LoginActivity
import com.fifo.match.ui.activity.otp.OtpActivity
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.utils.Constants
import com.fifo.match.network.utils.Constants.COUNTRY_NAME
import com.fifo.match.network.utils.Constants.Mobile_LOGIN
import com.fifo.match.network.utils.Constants.SIGN_FLAG
import com.fifo.match.network.utils.Constants.SIGN_GENDER
import com.fifo.match.network.utils.Constants.SIGN_INTEREST
import com.fifo.match.network.utils.Constants.SIGN_NAME
import com.fifo.match.ui.activity.aboutus.AboutsActivity
import com.fifo.match.utils.CardView
import com.fifo.match.utils.GpsTracker
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import com.github.hariprasanths.bounceview.BounceView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupActivity : BaseActivity<SignupViewModel, ActivitySignupBinding>(SignupViewModel::class.java) {

    var gender = arrayListOf("Man", "Woman", "Transgender")
    var interested = arrayListOf("Man", "Woman", "Transgender")

    override fun getLayoutRes() = R.layout.activity_signup
    private val viewModel by viewModels<SignupViewModel>()

    override fun initView() {
        setStatusBarTransparent("#FFFFFFFF", true)
        binding.viewModel = viewModel
        binding.activity = this
        getLocation()
        initListener()
        initRoundView()
        registerObservers()
    }

    private fun initRoundView() {
        binding.linearRoot.roundBorderedViewFromResId(15, R.color.white, R.color.gray, 1)
        binding.etFirstName.roundBorderedViewFromResId(15, R.color.white, R.color.gray, 1)
        binding.ssGender.roundBorderedViewFromResId(15, R.color.white, R.color.gray, 1)
        binding.ssInterset.roundBorderedViewFromResId(15, R.color.white, R.color.gray, 1)
        binding.country.roundBorderedViewFromResId(15, R.color.white, R.color.gray, 1)
        BounceView.addAnimTo(binding.ivNext)
        binding.tvTC.paintFlags = binding.tvTC.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.llOne.background = (CardView(
            resources.getColor(R.color.white), floatArrayOf(
                0f, 0f, 0f, 0f, 80f, 80f, 80f, 80f
            )
        ))

        binding.ssGender.setItem(gender)
        binding.ssInterset.setItem(interested)
    }

    private fun initListener() {
//        binding.ccp.setCountryForNameCode(getDefaultCountryCode(this))
        viewModel.stCountryCode.set(binding.ccp.selectedCountryCode)
        binding.apply {
            ccp.setOnCountryChangeListener {
                viewModel?.stCountryCode?.set(ccp.selectedCountryCode)
            }
            country.setOnCountryChangeListener {
                country.selectedCountryName
            }
            tvSignin.setOnClickListener {
                openActivity<LoginActivity>()
                finish()
            }
            tvTC.setOnClickListener {
                openActivity<AboutsActivity>(Constants.WEB_FLAG to Constants.TERMS_SERVICE)
            }
        }

    }

    fun onNext() {
        if (viewModel.stName.get().isNullOrEmpty()) {
            toastError(getString(R.string.enter_name))
        } else if (viewModel.stMobileNumber.get().isNullOrEmpty()) {
            toastError(getString(R.string.enter_phone_number))
        } else if (viewModel.stMobileNumber.get()!!.length < 4 || viewModel.stMobileNumber.get()!!.length >= 13) {
            toastError(getString(R.string.phone_number_valid))
        } else if (binding.ssGender.selectedItemPosition < 0) {
            toastError(getString(R.string.select_gender))
        } else if (binding.ssInterset.selectedItemPosition < 0) {
            toastError(getString(R.string.select_Inter))
        } else if (!binding.checkBox.isChecked) {
            toastError(getString(R.string.checkbox))
        } else {
            viewModel.callSignupApi()
        }

    }

    var otpx = ""
    private fun registerObservers() {
        viewModel.response.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<OtpModel>(it) {
                            /* otpx = otp.toString()*/
                        }
                    }
                    openActivity<OtpActivity>(
                        SIGN_FLAG to SIGN_FLAG,
                        Constants.CHECK_FLAG to "2",
                        Mobile_LOGIN to viewModel.stMobileNumber.get().toString(),
                        Constants.countryCode to viewModel.stCountryCode.get().toString(),
                        SIGN_NAME to binding.etFirstName.text.toString(),
                        SIGN_GENDER to binding.ssGender.selectedItem.toString(),
                        SIGN_INTEREST to binding.ssInterset.selectedItem.toString(),
                        COUNTRY_NAME to binding.country.selectedCountryName,
                        /*OTP_ to otpx,*/
                    )
                }
                is NetworkResult.Error -> {
                    ShowProgressDialog.hideProgress()
                    toastError(response.exception.localizedMessage)
                }

            }
        }
    }

    private fun getLocation() {
        val gpsTracker = GpsTracker(this)
        if (gpsTracker!!.canGetLocation()) {
        } else {
            gpsTracker!!.showSettingsAlert()
        }
    }

}