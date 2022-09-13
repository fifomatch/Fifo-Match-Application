package com.fifo.match.ui.activity.otp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.fifo.match.App
import com.fifo.match.R
import com.fifo.match.databinding.ActivityOtpBinding
import com.fifo.match.local.MyDataStore
import com.fifo.match.model.OtpModel
import com.fifo.match.model.SignupModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.utils.Constants
import com.fifo.match.ui.activity.chat.Constant
import com.fifo.match.ui.activity.completeProfile.CompleteProfileActivity
import com.fifo.match.ui.activity.home.HomeActivity
import com.fifo.match.ui.activity.membership.MembershipActivity
import com.fifo.match.ui.activity.survey.SurveyActivity
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.CardView
import com.fifo.match.utils.GpsTracker
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject


@AndroidEntryPoint
class OtpActivity : BaseActivity<OtpViewModel, ActivityOtpBinding>(OtpViewModel::class.java) {

    private var gpsTracker: GpsTracker? = null
    var mCountDownTimer: CountDownTimer? = null
    private var COUNTDOWN_INTERVAL = 1000
    var checkFlag = "0"

    @Inject
    lateinit var appDataStore: MyDataStore
    override fun getLayoutRes() = R.layout.activity_otp

    private val viewModel by viewModels<OtpViewModel>()

    override fun initView() {
        setStatusBarTransparent("#FFFFFFFF", true)
        binding.viewModel = viewModel
        binding.activity = this
        binding.llOne.background = (CardView(
            resources.getColor(R.color.white), floatArrayOf(
                0f, 0f, 0f, 0f, 80f, 80f, 80f, 80f
            )
        ))
        fcm()
        initListener()
        getLocation()
        registerObservers()
    }

    private fun initListener() {
        flowObservers()
        counterStart()
        if (intent.hasExtra(Constants.Mobile_LOGIN)) {
            checkFlag = intent.getStringExtra(Constants.CHECK_FLAG).toString()
            viewModel.stMobileNumber.set(intent.getStringExtra(Constants.Mobile_LOGIN).toString())
            viewModel.stCountryCode.set(intent.getStringExtra(Constants.countryCode).toString())
//            viewModel.stOtp.set(intent.getStringExtra(Constants.OTP_).toString())
        }
        if (intent.hasExtra(Constants.SIGN_FLAG)) {
            checkFlag = intent.getStringExtra(Constants.CHECK_FLAG).toString()
            viewModel.stMobileNumber.set(intent.getStringExtra(Constants.Mobile_LOGIN).toString())
            viewModel.stCountryCode.set(intent.getStringExtra(Constants.countryCode).toString())
            viewModel.stCountryName.set(intent.getStringExtra(Constants.COUNTRY_NAME).toString())
            viewModel.stName.set(intent.getStringExtra(Constants.SIGN_NAME).toString())
            viewModel.stGender.set(intent.getStringExtra(Constants.SIGN_GENDER).toString())
            viewModel.stInterested.set(intent.getStringExtra(Constants.SIGN_INTEREST).toString())
//            viewModel.stOtp.set(intent.getStringExtra(Constants.OTP_).toString())
        }
        binding.tvNumber.text = viewModel.stCountryCode.get() + "-" + viewModel.stMobileNumber.get()
        try {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.tvResendOTP.setOnClickListener {
            when (checkFlag) {
                "1" -> {
                    viewModel.callLoginApi()
                }
                "2" -> {
                    viewModel.callSignupApi()
                }
            }
        }
    }

    private fun fcm() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            viewModel.stDeviceToken.set(token)
        })
    }


    fun onNext() {
        if (viewModel.stOtp.get().isNullOrEmpty()) {
            toastError(getString(R.string.enter_top))
        } else {
            when (checkFlag) {
                "1" -> {
                    viewModel.callLoginVerifyApi()
                }
                "2" -> {
                    viewModel.callRegisterVerifyApi()
                }
            }
        }
    }

    private fun flowObservers() {
        viewModel.response.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    response.data?.result.let {
                        tryCast<SignupModel>(it) {
                            try {
                               lifecycleScope.launch(Dispatchers.IO) {
                                   viewModel.saveUser(this@tryCast)
                                   withContext(Dispatchers.Main) {
                                       ShowProgressDialog.hideProgress()
                                       if (intent.hasExtra(Constants.Mobile_LOGIN)){
                                           setPageProfile(this@tryCast)
                                       }
                                   }
                                   flowUserData()
                               }
                            } catch (e: Exception) {
                            }
                        }
                    }
                    openActivity<CompleteProfileActivity>()
                }
                is NetworkResult.Error -> {
                    ShowProgressDialog.hideProgress()
                    toastError(response.exception.localizedMessage!!.toString())
                }
            }
        }
    }

    private fun counterStart() {
        binding.tvResendOTP.isEnabled = false
        binding.tvResendOTP.setTextColor(ContextCompat.getColor(this,R.color.gray))
        mCountDownTimer = object : CountDownTimer(30000, COUNTDOWN_INTERVAL.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTimer.visibility = View.VISIBLE
                val second: Long = millisUntilFinished / 1000 % 60
                val minute: Long = millisUntilFinished / (1000 * 60) % 60
                if (second > 9) {
                    binding.tvTimer.text = "0$minute:$second"
                } else {
                    binding.tvTimer.text = "0$minute:0$second"
                }
            }
            override fun onFinish() {
                binding.tvTimer.text = "00:00"
                binding.tvResendOTP.isEnabled = true
                binding.tvResendOTP.setTextColor(ContextCompat.getColor(this@OtpActivity,R.color.blue))
            }
        }
        mCountDownTimer!!.start()
    }

    private fun getLocation() {
        gpsTracker = GpsTracker(this)
        if (gpsTracker!!.canGetLocation()) {
            val latitude: Double = gpsTracker!!.latitude
            val longitude: Double = gpsTracker!!.longitude
            viewModel.stLatitude.set(latitude.toString())
            viewModel.stLongitude.set(longitude.toString())
        } else {
            gpsTracker!!.showSettingsAlert()
        }
    }

    private fun setPageProfile(data: SignupModel) {
        if (data.profileComplete != null) {
            when (data.profileComplete) {
                0 -> {
                    openActivity<CompleteProfileActivity>(Constants.profileInt to "1")
                }
                1 -> {
                    openActivity<CompleteProfileActivity>(Constants.profileInt to "2")
                }
                2 -> {
                    setPageSurvey(data)
                }
            }
        }else{
            openActivity<CompleteProfileActivity>()
        }
        finish()
    }

    private fun setPageSurvey(data: SignupModel) {
        if (data.isSubscribed == true ){
            if (data.sureyStatus != null){
                when(data.sureyStatus){
                    0->{
                        openActivity<SurveyActivity>()
                    }
                    7->{
                        initFirebase()
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
                        initFirebase()
                        openActivity<HomeActivity>()
                    }
                    else->{
                        openActivity<SurveyActivity>()
                    }
                }
            }
        }
        else{
            openActivity<MembershipActivity>()
        }
        finish()
    }

     fun initFirebase() {
        FirebaseApp.initializeApp(this)
        GlobalScope.launch(Dispatchers.IO) {
            val isLoggedIn = appDataStore.isSessionStart()
            val signupModel = appDataStore.getUser()
            App.userBean = signupModel
            if (isLoggedIn) {
                App.mFirebaseAuth = FirebaseAuth.getInstance()
                App.mUserDatabase = FirebaseDatabase.getInstance().reference.child(Constant.FIREBASE_USER_TABLE)
                    .child(signupModel?.firebaseId.toString())
                App.mUserDatabase!!.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot != null) {
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
            }
        }
    }

    private fun flowUserData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                App.userBean = appDataStore.getUser()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun registerObservers() {
        viewModel.responseResend.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> { ShowProgressDialog.showProgress(this) }
                is NetworkResult.Success<*> -> { ShowProgressDialog.hideProgress()
                    response.data?.result?.let {}
                }
                is NetworkResult.Error -> { ShowProgressDialog.hideProgress()
                    toastError(response.exception.localizedMessage!!.toString())
                }
            }
        }
    }
}


