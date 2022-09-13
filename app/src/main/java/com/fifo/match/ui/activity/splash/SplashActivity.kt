package com.fifo.match.ui.activity.splash

import android.animation.ObjectAnimator
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.fifo.match.R
import com.fifo.match.databinding.ActivitySplashBinding
import com.fifo.match.local.MyDataStore
import com.fifo.match.model.SignupModel
import com.fifo.match.network.utils.Constants
import com.fifo.match.ui.activity.completeProfile.CompleteProfileActivity
import com.fifo.match.ui.activity.home.HomeActivity
import com.fifo.match.ui.activity.membership.MembershipActivity
import com.fifo.match.ui.activity.survey.SurveyActivity
import com.fifo.match.ui.activity.welcome.WelcomeActivity
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.extensions.openActivity
import com.fifo.match.utils.extensions.transparentStatusBar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivity<SplashViewModel, ActivitySplashBinding>(SplashViewModel::class.java) {

    override fun getLayoutRes() = R.layout.activity_splash
    private val viewModel by viewModels<SplashViewModel>()

    @Inject
    lateinit var appDataStore: MyDataStore

    override fun initView() {
        transparentStatusBar()
        binding.viewModel = viewModel
        ObjectAnimator.ofFloat(binding.root, View.ALPHA, 0.0f, 1.0f).setDuration(1000).start()
        viewModel.start()
        initObservables(viewModel)
    }

    private fun initObservables(viewModel: SplashViewModel) {
        viewModel.isFinished.observe(this, Observer {
            if (it) {
                lifecycleScope.launchWhenCreated {
                    val isLoggedIn = appDataStore.isSessionStart()
                    val signupModel=appDataStore.getUser()
                    if (isLoggedIn) {
                        when (signupModel?.profileComplete) {
                            0 -> {
                                openActivity<CompleteProfileActivity>(Constants.profileInt to "1")
                            }
                            1 -> {
                                openActivity<CompleteProfileActivity>(Constants.profileInt to "2")
                            }
                            2 -> {
                                setPageSurvey(signupModel)
                            }
                        }
                    } else {
                        openActivity<WelcomeActivity>()
                        finish()
                    }
                }
            }
        })
    }

    private fun setPageSurvey(data: SignupModel) {
        if (data.isSubscribed == true ){
            if (data.sureyStatus != null){
                when(data.sureyStatus){
                    0->{
                        openActivity<SurveyActivity>()
                    }
                    7->{
                        openActivity<HomeActivity>()
                    }
                    else->{
                        openActivity<SurveyActivity>()
                    }
                }
            }
        }else if(data.gender == "Woman"){
            if (data.sureyStatus != null){
                when(data.sureyStatus){
                    0->{
                        openActivity<MembershipActivity>()
                    }
                    7->{
                        openActivity<HomeActivity>()
                    }
                    else->{
                        openActivity<SurveyActivity>()
                    }
                }
            }
        }else{
            openActivity<MembershipActivity>()
        }
        finish()
    }
}