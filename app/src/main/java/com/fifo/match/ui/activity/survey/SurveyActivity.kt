package com.fifo.match.ui.activity.survey

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.fifo.match.R
import com.fifo.match.databinding.ActivitySurveyBinding
import com.fifo.match.local.MyDataStore
import com.fifo.match.model.SurveyModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.ui.activity.home.HomeActivity
import com.fifo.match.ui.activity.survey.adapter.SurveyViewPagerAdapter
import com.fifo.match.ui.activity.survey.fragment.*
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.CardView
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import com.github.hariprasanths.bounceview.BounceView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SurveyActivity : BaseActivity<SurveyViewModel, ActivitySurveyBinding>(SurveyViewModel::class.java) {

    private var viewPagerAdapter: SurveyViewPagerAdapter? = null

    @Inject
    lateinit var myDataStore: MyDataStore

    override fun getLayoutRes() = R.layout.activity_survey
    private val viewModel by viewModels<SurveyViewModel>()

    override fun initView() {
        transparentStatusBar()
        binding.viewModel = viewModel
        binding.activity = this
        BounceView.addAnimTo(binding.btnFirst)
        BounceView.addAnimTo(binding.btnNext)
        BounceView.addAnimTo(binding.btnPrevious)

        binding.btnFirst.roundBorderedViewFromResId(15, R.color.orange, R.color.orange, 1)
        binding.btnNext.roundBorderedViewFromResId(15, R.color.orange, R.color.orange, 1)
        binding.btnPrevious.roundBorderedViewFromResId(15, R.color.white, R.color.orange, 1)
        binding.consTop.background = (CardView(resources.getColor(R.color.white), floatArrayOf(80f, 80f, 80f, 80f, 0f, 0f, 0f, 0f)))
        setUpViewPager()
        registerObserver()
        viewModel.getSurveyDataApi()
        getSurveyDataObserver()
        viewModel.callActivePlan()
        planObserver()

    }

    fun onFirstNext() {
        val position = binding.viewpager.currentItem
        when (position) {
            0 -> {
                binding.btnFirst.gone()
                binding.llButton.visible()
                val activeFragment = viewPagerAdapter?.getItem(position)
                (activeFragment as SurveyFirstFragment).getSurveyFirst()
            }

        }
    }

    fun onNext() {
        val position = binding.viewpager.currentItem
        val activeFragment = viewPagerAdapter?.getItem(position)
        when (position) {
            1 -> {
                (activeFragment as SurveySecondFragment).getSurveySecond()
            }
            2 -> {
                (activeFragment as SurveyThreeFragment).getSurveyThree()
            }
            3 -> {
                (activeFragment as SurveyFourFragment).getSurveyFour()
            }
            4 -> {
                (activeFragment as SurveyFiveFragment).getSurveyFive()
            }
            5 -> {
                (activeFragment as SurveySixFragment).getSurveySix()
            }
            6 -> {
                (activeFragment as SurveySevenFragment).getSurveySeven()
            }
        }
    }

    private fun onBack(int: Int) {
        when (int) {
            1 -> {
                setCurrentItem(0)
                binding.llButton.gone()
                binding.btnFirst.visible()
            }
            2 -> {
                setCurrentItem(1)
            }
            3 -> {
                setCurrentItem(2)
            }
            4 -> {
                setCurrentItem(3)
            }
            5 -> {
                setCurrentItem(4)
            }
            6 -> {
                setCurrentItem(5)
            }
        }
    }

    override fun onBackPressed() {
        val currentItem: Int = binding.viewpager.currentItem
        if (currentItem != 0) {
            onBack(currentItem)
        } else {
            super.onBackPressed()
        }
    }

    fun onBackPage() {
        val position = binding.viewpager.currentItem
        if (position != 0) {
            onBack(position)
        } else {
            onBackPressed()
        }
    }

    fun onPrevious() {
        val position = binding.viewpager.currentItem
        onBack(position)
    }

    private fun setCurrentItem(position: Int) {
        viewModel.currentItem.set(position)
    }

    private fun setUpViewPager() {
        viewPagerAdapter = SurveyViewPagerAdapter(supportFragmentManager);
        viewPagerAdapter!!.add(SurveyFirstFragment())
        viewPagerAdapter!!.add(SurveySecondFragment())
        viewPagerAdapter!!.add(SurveyThreeFragment())
        viewPagerAdapter!!.add(SurveyFourFragment())
        viewPagerAdapter!!.add(SurveyFiveFragment())
        viewPagerAdapter!!.add(SurveySixFragment())
        viewPagerAdapter!!.add(SurveySevenFragment())
        binding.viewpager.adapter = viewPagerAdapter
    }

    private fun registerObserver() {
        viewModel.response.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        if (viewModel.performIndex.get()!! == 7) {
                            openActivity<HomeActivity>()
                            lifecycleScope.launch(Dispatchers.IO) {
                                userData?.sureyStatus = 7
                                myDataStore.updateUser(userData!!)
                            }
                        } else {
                            setCurrentItem(viewModel.performIndex.get()!!)
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

    private fun getSurveyDataObserver() {
        viewModel.responseT.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<SurveyModel>(it) {
                            viewModel.stSetData.value = this
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

    private fun planObserver() {
        viewModel.responsePlan.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
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