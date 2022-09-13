package com.fifo.match.ui.activity.welcome

import androidx.activity.viewModels
import com.fifo.match.R
import com.fifo.match.databinding.ActivityWelcomeBinding
import com.fifo.match.ui.activity.login.LoginActivity
import com.fifo.match.ui.activity.signup.SignupActivity
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.GpsTracker
import com.fifo.match.utils.extensions.openActivity
import com.fifo.match.utils.extensions.roundBorderedViewFromResId
import com.fifo.match.utils.extensions.transparentStatusBar
import com.github.hariprasanths.bounceview.BounceView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeActivity : BaseActivity<WelcomeViewModel, ActivityWelcomeBinding>(WelcomeViewModel::class.java) {

    private var gpsTracker: GpsTracker? = null
    override fun getLayoutRes() = R.layout.activity_welcome
    private val viewModel by viewModels<WelcomeViewModel>()

    override fun initView() {
        transparentStatusBar()
        binding.viewModel = viewModel
        binding.activity = this
        binding.btnRegister.roundBorderedViewFromResId(15, R.color.transperent, R.color.white, 1)
        binding.btnSignIn.roundBorderedViewFromResId(15, R.color.white, R.color.white, 1)
        BounceView.addAnimTo(binding.btnSignIn)
        BounceView.addAnimTo(binding.btnRegister)
        binding.btnSignIn.setOnClickListener {
            openActivity<LoginActivity>()
        }
        binding.btnRegister.setOnClickListener {
            openActivity<SignupActivity>()
        }
        binding.cons.post {
            getLocation()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun getLocation() {
        gpsTracker = GpsTracker(this)
        if (gpsTracker!!.canGetLocation()) {
        } else {
            gpsTracker!!.showSettingsAlert()
        }
    }

}


