package com.fifo.match.ui.activity.upgradeMembership

import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.fifo.match.R
import com.fifo.match.databinding.ActivityUpgradeMembershipBinding
import com.fifo.match.inAppPurchase.BillingCompleteListener
import com.fifo.match.inAppPurchase.InAppBilling
import com.fifo.match.inAppPurchase.PurchaseType
import com.fifo.match.model.CheckPlanModel
import com.fifo.match.model.PlanModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.ui.activity.home.HomeActivity
import com.fifo.match.ui.activity.home.fragment.profile.ProfileViewModel
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class UpgradeMembershipActivity : BaseActivity<ProfileViewModel, ActivityUpgradeMembershipBinding>(ProfileViewModel::class.java), BillingCompleteListener {

    private   val inAppBilling by lazy { InAppBilling(this, this) }
    private var isInAppBillingActive=false
    private val listOfSkuDetails= mutableListOf<SkuDetails>()

    private val listPlan = arrayListOf<PlanModel>()
    override fun getLayoutRes() = R.layout.activity_upgrade_membership
    private val viewModel by viewModels<ProfileViewModel>()

    override fun initView() {
        transparentStatusBar()
        binding.activity = this
        binding.btnUpgrade.roundBorderedViewFromResId(20, R.color.orange, R.color.orange, 1)

        viewModel.callActivePlan()
        viewModel.callPlanApi()
        planObserver()
        detailsPlanObservers()
        planPurchaseObservers()
        initInAppPurchasePayment()

        binding.btnUpgrade.setOnClickListener {
            if (binding.cbBox.isChecked){
                if(isInAppBillingActive){
                    inAppBilling.addSkuList("plan_id_02")
                    viewModel.stProductID.set("plan_id_02")
                    viewModel.stDateTime.set(viewModel.getCurrentDate())
                    viewModel.stAmount.set(listOfSkuDetails[1].price)
                }
            }else
                toastInfo("Please select plan")
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
                        tryCast<CheckPlanModel>(it) {
                            viewModel.stCheckPlan.value = this
                            setPlanData(this)
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

    private fun setPlanData(data: CheckPlanModel) {
        data.let {
            if (data != null) {
                val currentString = data.endDate
                val separated = currentString?.split("T")!!.toTypedArray()
                val planEndDate = separated[0]

                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val todayDate = sdf.format(Date())

                if (planEndDate < todayDate){
                    premiumPlanManage()
                    binding.tvPrise.text = listOfSkuDetails[1].price
                    binding.tv1.text = getString(R.string.premium_membership)
                    binding.iv1.setImageResource(R.drawable.ic_premium)
                    binding.tvExpire.gone()
                    binding.rl1.visible()
                }

                val expireDate = getDaysBetweenDates(getCurrentDate(), planEndDate, "yyyy-MM-dd")
                binding.tvExpire.text = "Expire in next $expireDate days."

                if (listOfSkuDetails.isNotEmpty()) {
                    if (data.planId.equals("plan_id_01")) {
                        binding.tvPrise.text = listOfSkuDetails[0].price
                        binding.iv1.setImageResource(R.drawable.ic_plan_logo)
                        binding.tv1.text = getString(R.string.standard_membership)
                        binding.tvPriseUpgrade.text = listOfSkuDetails[1].price
                        binding.tv2.text = getString(R.string.premium_membership)
                        binding.iv2.setImageResource(R.drawable.ic_premium)
                    }

                    if (data.planId.equals("plan_id_02")) {
                        premiumPlanManage()
                        binding.rl1.gone()
                        binding.tvPrise.text = listOfSkuDetails[1].price
                        binding.tv1.text = getString(R.string.premium_membership)
                        binding.iv1.setImageResource(R.drawable.ic_premium)
                    }
                }
            }
        }
    }

    private fun premiumPlanManage(){
        binding.tv12.gone()
        binding.clPlan.gone()
        binding.tv13.gone()
        binding.tvBenifits.gone()

    }

    private fun detailsPlanObservers() {
        viewModel.responsePlanDetails.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<ArrayList<PlanModel>>(it) {
                            listPlan.addAll(this)
                            if(listPlan.isNotEmpty()) {
                                listOfSkuDetails.forEachIndexed { index, it ->
                                    if(index<listPlan.size) {
                                        if (index == 1) {
                                            viewModel.stAmount.set(it.price)
                                            val name = listPlan[0].name?.split(" ")?.get(0).toString()
                                            listPlan[0].name = "$name ${it.price}"
                                        } else if (index == 0) {
                                            val name = listPlan[1].name?.split(" ")?.get(0).toString()
                                            listPlan[1].name = "$name ${it.price}"
                                        }
                                    }
                                }
                            }
                            val strToHtml = Html.fromHtml(this[0].cmsBody)
                            binding.tvBenifits.text = strToHtml
                            binding.tvBenifits.movementMethod = LinkMovementMethod.getInstance();
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

    private fun getDaysBetweenDates(firstDateValue: String, secondDateValue: String, format: String): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        val firstDate = sdf.parse(firstDateValue)
        val secondDate = sdf.parse(secondDateValue)
        if (firstDate == null || secondDate == null)
            return 0.toString()
        return (((secondDate.time - firstDate.time) / (1000 * 60 * 60 * 24)) + 1).toString()
    }

    private fun planPurchaseObservers() {
        viewModel.responsePurchasePlan.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        openActivity<HomeActivity>()
                        finish()
                        lifecycleScope.launch(Dispatchers.IO) {
                            userData?.isSubscribed = true
//                            myDataStore.updateUser(userData!!)
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

    private fun initInAppPurchasePayment() {
        ShowProgressDialog.showProgress(this)
        inAppBilling.initializeBillingClient {
            runOnUiThread {
                ShowProgressDialog.hideProgress()
            }
            isInAppBillingActive=it
            if (it){
                inAppBilling.addAllSkuList(listOf("plan_id_01","plan_id_02"),PurchaseType.SUBSCRIPTION){
                    listOfSkuDetails.addAll(it)
                }
            }
        }
    }

    override fun billingComplete(purchases: List<Purchase?>?) {
        runOnUiThread {
            toastInfo("billingComplete")
            viewModel.callSaveTransaction()
        }

    }

    override fun billingFailed() {
        toastInfo("billingFailed")
    }

    override fun billingCanceled() {
        toastInfo("billingCanceled")
    }

    override fun billingAlreadyPurchased() {
        toastInfo("billingAlreadyPurchased")
    }

}