package com.fifo.match.ui.activity.completeProfile

import androidx.activity.viewModels
import com.fifo.match.R
import com.fifo.match.databinding.ActivityCompleteProfileBinding
import com.fifo.match.network.utils.Constants
import com.fifo.match.ui.activity.completeProfile.adapter.ViewPagerAdapter
import com.fifo.match.ui.activity.completeProfile.fragment.ProfileFirstFragment
import com.fifo.match.ui.activity.completeProfile.fragment.ProfileSecondFragment
import com.fifo.match.ui.activity.membership.MembershipActivity
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.CardView
import com.fifo.match.utils.extensions.*
import com.github.hariprasanths.bounceview.BounceView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompleteProfileActivity : BaseActivity<CompleteProfileViewModel, ActivityCompleteProfileBinding>(CompleteProfileViewModel::class.java) {

    private var viewPagerAdapter: ViewPagerAdapter? = null
    override fun getLayoutRes() = R.layout.activity_complete_profile
    private val viewModel by viewModels<CompleteProfileViewModel>()
    var pageOpen = "0"

    override fun initView() {
        transparentStatusBar()
        binding.viewModel = viewModel
        binding.activity = this
        BounceView.addAnimTo(binding.btnSave)
        binding.btnSave.roundBorderedViewFromResId(15, R.color.orange, R.color.orange, 1)
        binding.consTop.background = (CardView(resources.getColor(R.color.white), floatArrayOf(80f, 80f, 80f, 80f, 0f, 0f, 0f, 0f)))
        setUpViewPager()
        if (intent.hasExtra(Constants.profileInt)){
            pageOpen = intent.getStringExtra(Constants.profileInt).toString()
            pageFind(pageOpen)
        }


        binding.toolbar.ivBack.gone()

        binding.toolbar.ivBack.setOnClickListener {
            changePage(0)
        }

    }

   private fun pageFind(pageOpen: String) {
        when(pageOpen){
            "1"->{
                changePage(0)
            }
            "2"->{
                changePage(1)
            }
        }
    }

    fun onNext() {
        when (val position = binding.viewpager.currentItem) {
            0 -> {
                val activeFragment = viewPagerAdapter?.getItem(position)
                (activeFragment as ProfileFirstFragment).getProfileFirstData()
            }
            1 -> {
                val activeFragment = viewPagerAdapter?.getItem(position)
                (activeFragment as ProfileSecondFragment).getPhotos()
            }
        }
    }

    private fun changePage(position: Int) {
        when (position) {
            0 -> {
                setCurrentItem(0)
                binding.toolbar.ivBack.gone()
                binding.toolbar.tvStep.text = getString(R.string.steps_1_2)
                binding.toolbar.tvProgressValue.text = getString(R.string._50_completed)
            }
            1 -> {
                setCurrentItem(1)
                binding.toolbar.ivBack.visible()
                binding.toolbar.tvStep.text = getString(R.string.steps_2_2)
                binding.toolbar.tvProgressValue.text = getString(R.string._60_completed)
            }
        }
    }

    private fun setCurrentItem(position: Int) {
        binding.viewpager.currentItem=position
    }

    private fun setUpViewPager() {
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager);
        viewPagerAdapter!!.add(ProfileFirstFragment.newInstance())
        viewPagerAdapter!!.add(ProfileSecondFragment.newInstance())
        binding.viewpager.adapter = viewPagerAdapter
    }

    override fun onBackPressed() {
        val currentItem: Int = binding.viewpager.currentItem
        if (currentItem != 0) {
            changePage(0)
        } else {
            super.onBackPressed()
            finishAffinity()
        }
    }

    fun redirectToSecondFragment() {
     changePage(1)
    }

    fun redirectToMembership() {
        openActivity<MembershipActivity>()
    }


}