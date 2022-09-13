package com.fifo.match.ui.activity.home

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.fifo.match.App
import com.fifo.match.App.Companion.mUserDatabase
import com.fifo.match.R
import com.fifo.match.databinding.ActivityHomeBinding
import com.fifo.match.databinding.DialogChatBinding
import com.fifo.match.network.NetworkResult
import com.fifo.match.ui.activity.chat.Constant
import com.fifo.match.ui.activity.completeProfile.fragment.ProfileSecondFragment
import com.fifo.match.ui.activity.home.fragment.LikeFragment
import com.fifo.match.ui.activity.home.fragment.chat.ChatFragment
import com.fifo.match.ui.activity.home.fragment.homeFragment.HomeFragment
import com.fifo.match.ui.activity.home.fragment.profile.ProfileFragment
import com.fifo.match.ui.activity.home.fragment.save.SaveFragment
import com.fifo.match.ui.activity.membership.MembershipActivity
import com.fifo.match.ui.activity.welcome.WelcomeActivity
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.extensions.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseActivity<HomeViewModel, ActivityHomeBinding>(HomeViewModel::class.java) {

    var doubleBackToExitPressedOnce = false
    private val viewModel by viewModels<HomeViewModel>()

    override fun getLayoutRes() = R.layout.activity_home

    override fun initView() {
        transparentStatusBar()
        binding.viewModel = viewModel
        binding.activity = this
        binding.dashboardFrame.post {
            binding.bottomNav.selectedItemId = R.id.home
        }
        liveUpdateObservers()
        liveUpdateAPI()
        repeatCall()

    }

    fun onBottomMenuClicked(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                replaceFragment(HomeFragment(), R.id.dashboardFrame)
                return true
            }
            R.id.save -> {
                replaceFragment(SaveFragment(), R.id.dashboardFrame)
                return true
            }
            R.id.message -> {
                replaceFragment(ChatFragment(), R.id.dashboardFrame)
                return true
            }
            R.id.notification -> {
                replaceFragment(LikeFragment(), R.id.dashboardFrame)
                return true
            }
            R.id.profile -> {
                replaceFragment(ProfileFragment(), R.id.dashboardFrame)
                return true
            }
        }
        return false
    }


    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finishAffinity()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Snackbar.make(binding.root, "Tap again to exit app", Snackbar.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    private fun liveUpdateAPI(){
        viewModel.callLiveUpdateApi()
    }

    private fun liveUpdateObservers() {
        viewModel.response.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {}
                is NetworkResult.Success<*> -> {
                    response.data?.let {
                        if (it.message == "Your plan is expired."){
                            openActivity<MembershipActivity>()
                            finish()
                            userData?.isSubscribed = false
                        }
                    }
                }
                is NetworkResult.Error -> {
                    if (response.exception.localizedMessage!!.toString() == "Token is Invalid"){
                        openActivity<WelcomeActivity>()
                        finish()
                        lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.clearSession()
                        }
                    }
                }
            }
        }
    }

    fun repeatCall() {
        object : CountDownTimer(50000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {
                repeatCall()//again call your method
                liveUpdateAPI()
            }
        }.start()
    }


}