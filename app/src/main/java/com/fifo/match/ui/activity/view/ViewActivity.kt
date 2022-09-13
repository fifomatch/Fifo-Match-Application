package com.fifo.match.ui.activity.view

import android.graphics.Color
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import com.fifo.match.R
import com.fifo.match.databinding.ActivityViewBinding
import com.fifo.match.model.CheckPlanModel
import com.fifo.match.model.UserDetailsModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.utils.Constants
import com.fifo.match.ui.activity.home.adapter.TabPagerAdapter
import com.fifo.match.ui.activity.view.adapter.ImageViewPagerAdapter
import com.fifo.match.ui.activity.view.fragment.InfoFragment
import com.fifo.match.ui.activity.view.fragment.PhotoFragment
import com.fifo.match.ui.activity.view.fragment.SurveyAnswersFragment
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.GpsTracker
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.gone
import com.fifo.match.utils.extensions.toastError
import com.fifo.match.utils.extensions.tryCast
import com.fifo.match.utils.extensions.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_view.*

@AndroidEntryPoint
class ViewActivity : BaseActivity<ViewViewModel, ActivityViewBinding>(ViewViewModel::class.java) {

    private var gpsTracker: GpsTracker? = null
    private val listPhotos = arrayListOf<UserDetailsModel.UserDetails.Photo>()
    private var pagerAdapter : ImageViewPagerAdapter? = null
    var isLiked = false
    var isSaved = false
    var isCancel = false

    override fun getLayoutRes() = R.layout.activity_view
    private val viewModel by viewModels<ViewViewModel>()

    override fun initView() {
        fullScreen()
        binding.activity = this
        binding.viewModel = viewModel
        getLocation()
        if (intent.hasExtra(Constants.PROFILE_FLAG)) {
            viewModel.stUserID.set(intent.getStringExtra(Constants.PROFILE))
        }
        viewModel.callUserData()
        viewModel.callActivePlan()
        setupViewPager()
        getObserver()
        planObserver()
        getLikeObserver()
        viewModel.stStatus.set("hide")
    }

    private fun initListeners() {

        binding.icCancel.setOnClickListener {
            isCancel = true
            viewModel.stStatus.set("cancel")
            viewModel.callSaveLikeCancel()
        }

        binding.ivSave.setOnClickListener {
            isCancel = false
           if (isSaved){
               isSaved = false
               viewModel.stStatus.set("saved")
               viewModel.callSaveLikeCancel()
               binding.ivSave.setImageResource(R.drawable.ic_save_select)
           }else{
               isSaved = true
               viewModel.stStatus.set("saved")
               viewModel.callRemoveUser()
               binding.ivSave.setImageResource(R.drawable.ic_save_unselect)
           }
        }

        binding.ivLike.setOnClickListener {
            if (isLiked){
                isLiked = false
                viewModel.callUserLike()
                binding.ivLike.setImageResource(R.drawable.ic_like_oarange)
            }else{
                isLiked = true
                viewModel.callUserDislike()
                binding.ivLike.setImageResource(R.drawable.ic_like_select)
            }
        }

        binding.swSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            isCancel = false
            if (isChecked) {
                viewModel.callSaveLikeCancel()
            } else {
                viewModel.callRemoveUser()
            }
        }

    }

    private fun setupPagerPhotos() {
        pagerAdapter = ImageViewPagerAdapter(this,listPhotos)
        binding.vpImage.adapter = pagerAdapter
        binding.vpImage.currentItem = 1;
        binding.dotsIndicator.setViewPager(binding.vpImage)
        binding.tabLayout.setupWithViewPager(binding.tabViewPager)
    }

    private fun fullScreen() {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun setupViewPager() {
        val adapter = TabPagerAdapter(supportFragmentManager)
        adapter.addFragment(InfoFragment(), "Info")
        adapter.addFragment(PhotoFragment(), "Photos")
        adapter.addFragment(SurveyAnswersFragment(), "Survey Answers")
        binding.tabViewPager.adapter = adapter
        binding.tabViewPager.offscreenPageLimit=10
    }

    private fun getObserver() {
        viewModel.responseUser.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<UserDetailsModel>(it){
                            viewModel.stSetData.value = this
                            setData(this.userDetails)
                            listPhotos.addAll(this.userDetails!!.photos)
                            setupPagerPhotos()
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

    private fun setData(data: UserDetailsModel.UserDetails?) {
        binding.apply {
            tvName.text = data?.name + ","
            tvAge.text = data?.age.toString()
            tvLocation.text = data?.countryName.toString()
            tvMils.text = data?.miles.toString()
            tvDesc.text = data?.about.toString()
            if (data?.isVerified != null){
                if (data.isVerified == 2){
                    ivVerify.visible()
                }else ivVerify.gone()
            }
            if (data?.userInappTransaction != null){
                if (data.userInappTransaction.planId.equals("plan_id_01")){
                    binding.ivLogo.setImageResource(R.drawable.ic_plan_logo)
                }else if (data.userInappTransaction.planId.equals("plan_id_02")) {
                    binding.ivLogo.setImageResource(R.drawable.ic_premium)
                }
            }
            binding.swSwitch.isChecked = data?.isBlocked == true

            if (data?.isLike == true){
                isLiked = false
                ivLike.setImageResource(R.drawable.ic_like_oarange)
            }else{
                isLiked = true
                ivLike.setImageResource(R.drawable.ic_like_select)
            }
            if (data?.isSaved == true){
                isSaved = false
                ivSave.setImageResource(R.drawable.ic_save_select)
            }else{
                isSaved = true
                ivSave.setImageResource(R.drawable.ic_save_unselect)
            }

        }
        initListeners()
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

    private fun planObserver() {
        viewModel.responsePlan.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<CheckPlanModel>(it){
                            viewModel.stCheckPlan.value = this
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

    private fun getLikeObserver() {
        viewModel.responseLike.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    if (isCancel){
                        onBackPressed()
                        isCancel = false
                    }
                    response.data?.result?.let {
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
