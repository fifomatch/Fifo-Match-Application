package com.fifo.match.ui.activity.home.fragment.homeFragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.fifo.match.R
import com.fifo.match.databinding.BottomsheetFilterBinding
import com.fifo.match.databinding.FragmentHomeBinding
import com.fifo.match.model.CompleteProfileListBean
import com.fifo.match.model.MatchListModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.ui.activity.home.adapter.BodyTypeFilterAdapter
import com.fifo.match.ui.activity.home.adapter.TabPagerAdapter
import com.fifo.match.ui.activity.membership.MembershipActivity
import com.fifo.match.ui.activity.notification.NotificationActivity
import com.fifo.match.ui.activity.welcome.WelcomeActivity
import com.fifo.match.ui.base.BaseFragment
import com.fifo.match.utils.CardView
import com.fifo.match.utils.GpsTracker
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import com.github.hariprasanths.bounceview.BounceView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<HomeFragViewModel, FragmentHomeBinding>(HomeFragViewModel::class.java) {

    val listOfRelation = mutableListOf<String>()
    val listOfOccupationsData = mutableListOf<String>()
    val listOfEducation = mutableListOf<String>()
    private var profileListBean: CompleteProfileListBean? = null

    var listBody = arrayListOf("Slim", "Athletic ", "A few pounds")
    private val adapter = BodyTypeFilterAdapter(listBody)

    private var gpsTracker: GpsTracker? = null
    private var data: MatchListModel? = null
    private val viewModel: HomeFragViewModel by activityViewModels()

    var minAge: Int = 18
    var maxAge: Int = 70
    var valueData = 0
    var latitude: Double? = null
    var longitude: Double? = null

    override fun getLayoutRes() = R.layout.fragment_home

    override fun onRender() {
        super.onRender()
        binding.fragment = this
        binding.viewMdoel = viewModel
        getLocation()
        binding.llShape.background = (CardView(
            resources.getColor(R.color.light_white), floatArrayOf(
                80f, 80f, 80f, 80f, 0f, 0f, 0f, 0f
            )
        ))
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        setupViewPager()

        binding.filter.setOnClickListener {
            bottomSheetFilter()
        }
        binding.ivNotification.setOnClickListener {
            requireActivity().openActivity<NotificationActivity>()
        }
        callApi()
        getObserver()
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            callApi()
        }
        viewModel.getCompleteDataList()
        registerSpinnerObservers()
    }

    private fun setupViewPager() {
        val adapter = TabPagerAdapter(childFragmentManager)
        adapter.addFragment(OnlineFragment(), "Online")
        adapter.addFragment(PremiumFragment(), "Premium")
        adapter.addFragment(NewFragment(), "New")
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 10
    }

    private fun callApi() {
        viewModel.getMatchListApi()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMatchListApi()
    }

    private fun registerSpinnerObservers() {
        viewModel.responseSpinner.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(activity)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    tryCast<CompleteProfileListBean>(response.data?.result) {
                        profileListBean = this
                        profileListBean?.let {
                            setSpinnerData(this)
                        }
                    }
                }
                is NetworkResult.Error -> {
                    ShowProgressDialog.hideProgress()
                }
            }
        }

    }

    private fun getObserver() {
        viewModel.responseTT.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    binding.swipeRefresh.isRefreshing = false
                    ShowProgressDialog.showProgress(activity)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    if (response.data?.message == "Your plan is expired.") {
                        requireActivity().openActivity<MembershipActivity>()
                        requireActivity().finish()
                        userData?.isSubscribed = false
                    }
                    binding.swipeRefresh.isRefreshing = false
                    response.data?.result?.let {
                        tryCast<MatchListModel>(it) {
                            binding.swipeRefresh.isRefreshing = false
                            data = this
                            viewModel.stSetData.value = this
                        }
                    }
                }
                is NetworkResult.Error -> {
                    ShowProgressDialog.hideProgress()
                    if (response.exception.localizedMessage!!.toString() == "Token is Invalid") {
                        requireActivity().openActivity<WelcomeActivity>()
                        requireActivity().finish()
                        lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.clearSession()
                        }
                    }
                }
            }
        }
    }

    private fun getLocation() {
        gpsTracker = GpsTracker(requireActivity())
        if (gpsTracker!!.canGetLocation()) {
            latitude = gpsTracker!!.latitude
            longitude = gpsTracker!!.longitude

        } else {
            gpsTracker!!.showSettingsAlert()
        }
    }

    private fun setSpinnerData(profileListBean: CompleteProfileListBean) {
        profileListBean.relationshipStatusData?.forEach {
            listOfRelation.add(it?.name.toString())
        }
        profileListBean.occupationsData?.forEach {
            listOfOccupationsData.add(it?.name.toString())
        }
        profileListBean.educationData?.forEach {
            listOfEducation.add(it?.name.toString())
        }
    }

    private fun bottomSheetFilter() {
        val binding: BottomsheetFilterBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()),
            R.layout.bottomsheet_filter,
            null,
            false
        )
        val dialog = BottomSheetDialog(requireActivity(), R.style.AppBottomSheetDialogThemeXX)
        binding.fragment = this
        BounceView.addAnimTo(dialog);
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        binding.btnFilter.roundBorderedViewFromResId(15, R.color.orange, R.color.orange, 1)

        setBottomSheetFilterListeners(binding, dialog)

//        binding.seekBarAge.setRange(minAge.toFloat(), maxAge.toFloat())
        dialog.setContentView(binding.root)
        dialog.show()
    }

    private fun setBottomSheetFilterListeners(
        bindingSheet: BottomsheetFilterBinding,
        dialog: BottomSheetDialog
    ) {
        /** Apply Filter **/
        bindingSheet.btnFilter.setOnClickListener {
            callApi()
            dialog.dismiss()
        }

        /** Dialog Dismiss**/
        bindingSheet.ivCancel.setOnClickListener {
            dialog.dismiss()
        }

        /** Reset Filter * */
        bindingSheet.btnReset.setOnClickListener {
            viewModel.stLat.set("")
            viewModel.stCountry.set("")
            viewModel.stLong.set("")
            viewModel.stDistance.set("")
            viewModel.stHeightType.set("")
            viewModel.stHeight.set("")
            viewModel.stRelationshipID.set("")
            viewModel.stOccupationID.set("")
            viewModel.stMinAge.set("")
            viewModel.stMaxAge.set("")
            viewModel.stBodyType.set("")
            viewModel.stFifo.set("")
            viewModel.stInterestedIn.set("")
            callApi()
            dialog.dismiss()
        }

        /** Advance Filter */

        bindingSheet.rlAdvanceFilter.setOnClickListener {
            if (bindingSheet.rlAdvanceFilter.isSelected) {
                bindingSheet.rlAdvanceFilter.isSelected = false
                bindingSheet.clAdvancedFilter.visible()
            } else {
                bindingSheet.rlAdvanceFilter.isSelected = true
                bindingSheet.clAdvancedFilter.gone()
            }
        }

        bindingSheet.btnMen.setOnClickListener {
            onInterested(bindingSheet, 1)
        }

        bindingSheet.btnWomen.setOnClickListener {
            onInterested(bindingSheet, 2)
        }

        bindingSheet.btnEveryone.setOnClickListener {
            onInterested(bindingSheet, 3)
        }

        bindingSheet.btnYes.setOnClickListener {
            onFifo(bindingSheet, 1)
        }

        bindingSheet.btnNo.setOnClickListener {
            onFifo(bindingSheet, 2)
        }

        bindingSheet.country.setOnCountryChangeListener {
            viewModel.stCountry.set(bindingSheet.country.selectedCountryName)
        }

        /***  Distance range *  */
        if (viewModel.stDistance.get().toString().isNotEmpty()) {
            bindingSheet.seekRange.progress = viewModel.stDistance.get()?.toInt()!!
            bindingSheet.tvRange.text = "" + viewModel.stDistance.get() + " km"
        }

        bindingSheet.seekRange.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.stLat.set(latitude.toString())
                viewModel.stLong.set(longitude.toString())
                viewModel.stDistance.set(progress.toString())
                bindingSheet.tvRange.text = "" + viewModel.stDistance.get() + " km"

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        /***  Height * */
        if (viewModel.stHeightType.get().toString().isNotEmpty()) {
            if (viewModel.stHeightType.get() == "cm") {
                bindingSheet.swDistanceType.isChecked = false
                bindingSheet.txtValueHieght.text = viewModel.stHeight.get() + "cm"
            } else {
                bindingSheet.swDistanceType.isChecked = true
                bindingSheet.txtValueHieght.text =
                    cmToFeet(viewModel.stHeight.get()!!.toInt()) + "″"
            }
            bindingSheet.seekHeight.progress = viewModel.stHeight.get()!!.toInt() - 120
        }

        bindingSheet.swDistanceType.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                valueData = 120 + bindingSheet.seekHeight.progress
                viewModel.stHeight.set(valueData.toString())
                if (isChecked) {
                    viewModel.stHeightType.set("inches")
                    bindingSheet.txtValueHieght.text =
                        cmToFeet(viewModel.stHeight.get()!!.toInt()) + "″"
                    bindingSheet.tvCM.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.gray
                        )
                    )
                    bindingSheet.tvInches.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                } else {
                    viewModel.stHeightType.set("cm")
                    bindingSheet.txtValueHieght.text = "" + valueData + " cm"
                    bindingSheet.tvInches.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.gray
                        )
                    )
                    bindingSheet.tvCM.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                }
            }
        }

        bindingSheet.seekHeight.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                valueData = 120 + progress
                viewModel.stHeight.set(valueData.toString())
                if (viewModel.stHeightType.get().equals("inches")) {
                    bindingSheet.txtValueHieght.text =
                        cmToFeet(viewModel.stHeight.get()!!.toInt()) + "″"
                } else {
                    bindingSheet.txtValueHieght.text = "" + viewModel.stHeight.get() + "cm"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        /** Spinner * */

        bindingSheet.etStatus.item = listOfRelation as List<String>
        bindingSheet.etOccupation.item = listOfOccupationsData as List<String>
        bindingSheet.etStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                viewModel.stRelationshipID.set(profileListBean?.relationshipStatusData?.get(position)?.id.toString())
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        bindingSheet.etOccupation.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    viewModel.stOccupationID.set(profileListBean?.occupationsData?.get(position)?.id.toString())
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            }

        /** List Body type */

        bindingSheet.rvBodyType.adapter = adapter
        bindingSheet.rvBodyType.affectOnItemClicks { position, view ->
            viewModel.stBodyType.set(listBody[position])
        }

        /** Age Range*/
        if (viewModel.stMinAge.get().toString().isNotEmpty() && viewModel.stMaxAge.get().toString()
                .isNotEmpty()
        ) {
            bindingSheet.seekBarAge.post {
                bindingSheet.seekBarAge.setProgress(
                    viewModel.stMinAge.get()!!.toFloat(),
                    viewModel.stMaxAge.get()!!.toFloat()
                )
            }
        }

        bindingSheet.seekBarAge.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(
                view: RangeSeekBar?,
                leftValue: Float,
                rightValue: Float,
                isFromUser: Boolean
            ) {
                minAge = leftValue.toInt()
                maxAge = rightValue.toInt()
                if (maxAge < 70) {
                    bindingSheet.hintAgeRange.text =
                        "".plus(leftValue.toInt()).plus(" - ").plus(rightValue.toInt())
                } else {
                    bindingSheet.hintAgeRange.text =
                        "".plus(leftValue.toInt()).plus(" - ").plus("70+")
                }
                viewModel.stMinAge.set(leftValue.toString())
                viewModel.stMaxAge.set(rightValue.toString())
            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {}
            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {}
        })

        /** Set Filter Data * */
        when {
            viewModel.stInterestedIn.get() == "Man" -> {
                onInterested(bindingSheet, 1)
            }
            viewModel.stInterestedIn.get() == "Woman" -> {
                onInterested(bindingSheet, 2)
            }
            viewModel.stInterestedIn.get() == "Transgender" -> {
                onInterested(bindingSheet, 3)
            }
        }

        when {
            viewModel.stFifo.get() == "Yes" -> {
                onFifo(bindingSheet, 1)
            }
            viewModel.stFifo.get() == "No" -> {
                onFifo(bindingSheet, 2)
            }
        }

        if (viewModel.stRelationshipID.get().toString().isNotEmpty()) {
            bindingSheet.etStatus.post {
                bindingSheet.etStatus.setSelection(
                    viewModel.stRelationshipID.get()!!.toInt() - 1,
                    true
                )
            }
        }

        if (viewModel.stOccupationID.get().toString().isNotEmpty()) {
            bindingSheet.etOccupation.post {
                bindingSheet.etOccupation.setSelection(
                    viewModel.stOccupationID.get()!!.toInt() - 1,
                    true
                )
            }
        }

        if (viewModel.stBodyType.get().toString().isNotEmpty()) {
            adapter.setSelectedData(viewModel.stBodyType.get().toString())
        }

    }


    private fun onInterested(mBinding: BottomsheetFilterBinding, int: Int) {
        when (int) {
            1 -> {
                viewModel.stInterestedIn.set(getString(R.string.men))
                mBinding.btnMen.background =
                    requireActivity().getDrawablexx(R.drawable.shape_select)
                mBinding.btnWomen.background =
                    requireActivity().getDrawablexx(R.drawable.shape_unselect)
                mBinding.btnEveryone.background =
                    requireActivity().getDrawablexx(R.drawable.shape_unselect)
                mBinding.btnMen.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.orange
                    )
                );
                mBinding.btnWomen.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.gray
                    )
                );
                mBinding.btnEveryone.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.gray
                    )
                );
            }
            2 -> {
                viewModel.stInterestedIn.set(getString(R.string.women))
                mBinding.btnEveryone.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.gray
                    )
                );
                mBinding.btnWomen.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.orange
                    )
                );
                mBinding.btnWomen.background =
                    requireActivity().getDrawablexx(R.drawable.shape_select)
                mBinding.btnMen.background =
                    requireActivity().getDrawablexx(R.drawable.shape_unselect)
                mBinding.btnEveryone.background =
                    requireActivity().getDrawablexx(R.drawable.shape_unselect)
                mBinding.btnMen.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.gray
                    )
                );
            }
            3 -> {
                viewModel.stInterestedIn.set(getString(R.string.everyone))
                mBinding.btnEveryone.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.orange
                    )
                );
                mBinding.btnMen.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.gray
                    )
                );
                mBinding.btnWomen.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.gray
                    )
                );
                mBinding.btnEveryone.background =
                    requireActivity().getDrawablexx(R.drawable.shape_select)
                mBinding.btnWomen.background =
                    requireActivity().getDrawablexx(R.drawable.shape_unselect)
                mBinding.btnMen.background =
                    requireActivity().getDrawablexx(R.drawable.shape_unselect)
            }
        }
    }

    private fun onFifo(mBinding: BottomsheetFilterBinding, int: Int) {
        when (int) {
            1 -> {
                mBinding.btnYes.background =
                    requireActivity().getDrawablexx(R.drawable.shape_select)
                mBinding.btnNo.background =
                    requireActivity().getDrawablexx(R.drawable.shape_unselect)
                mBinding.btnYes.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.orange
                    )
                )
                mBinding.btnNo.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.gray
                    )
                );
                viewModel.stFifo.set(getString(R.string.yes))
            }
            2 -> {
                mBinding.btnNo.background = requireActivity().getDrawablexx(R.drawable.shape_select)
                mBinding.btnYes.background =
                    requireActivity().getDrawablexx(R.drawable.shape_unselect)
                mBinding.btnNo.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.orange
                    )
                );
                mBinding.btnYes.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.gray
                    )
                );
                viewModel.stFifo.set(getString(R.string.no))
            }
        }
    }

    fun cmToFeet(centi: Int): String {
        val inch: Double = 0.3937 * centi
        val feet: Double = inch / 12
        val remainInch: Double = inch % 12
        return "".plus(feet.toInt()).plus("'").plus(remainInch.toInt())
    }

}