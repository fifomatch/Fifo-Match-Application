package com.fifo.match.ui.activity.membership

import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.fifo.match.R
import com.fifo.match.databinding.ActivityMembershipBinding
import com.fifo.match.inAppPurchase.BillingCompleteListener
import com.fifo.match.inAppPurchase.InAppBilling
import com.fifo.match.inAppPurchase.PurchaseType
import com.fifo.match.local.MyDataStore
import com.fifo.match.model.PlanModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.ui.activity.congratulations.CongratulationsActivity
import com.fifo.match.ui.activity.membership.adapter.PlanAdapter
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import com.github.hariprasanths.bounceview.BounceView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MembershipActivity : BaseActivity<MembershipViewModel,ActivityMembershipBinding>(MembershipViewModel::class.java),
    BillingCompleteListener {
    private   val inAppBilling by lazy { InAppBilling(this, this) }
    private var isInAppBillingActive=false
    private val listOfSkuDetails= mutableListOf<SkuDetails>()
    var isPlan = false

    private val listPlan = arrayListOf<PlanModel>()

    private var adapterPlan  : PlanAdapter? = null
    @Inject
    lateinit var myDataStore: MyDataStore
    override fun getLayoutRes() = R.layout.activity_membership
    private val viewModel by viewModels<MembershipViewModel>()

    override fun initView() {
        setStatusBarTransparent("#FFFFFFFF", true)
        binding.viewModel = viewModel
        binding.activity = this

        initListeners()
        planObservers()
        getPlanApi()
        planGetObservers()
        initInAppPurchasePayment()
    }

    private fun setupRv() {
        if(isPlan) listPlan.removeAt(1)
        adapterPlan = PlanAdapter(listPlan)
        binding.rvPlan.adapter = adapterPlan

        binding.rvPlan.affectOnItemClicks { position, view ->
            if (position == 0){
                viewModel.stAmount.set(listOfSkuDetails[position+1].price)
                viewModel.stProductID.set("plan_id_02")
            }else {
                viewModel.stAmount.set(listOfSkuDetails[position-1].price)
                viewModel.stProductID.set("plan_id_01")
            }
            val strToHtml = Html.fromHtml(listPlan[position].cmsBody)
            binding.tvBenifits.text = strToHtml

        }
    }

    private  fun initListeners() {
        BounceView.addAnimTo(binding.btnContinue)
        binding.btnContinue.roundBorderedViewFromResId(20, R.color.orange, R.color.orange, 1)
        binding.btnSkip.roundBorderedViewFromResId(20, R.color.orange, R.color.orange, 1)

        binding.btnContinue.setOnClickListener {
            if(isInAppBillingActive)
                inAppBilling.addSkuList(viewModel.stProductID.get().toString())
//            toast(viewModel.stProductID.get().toString())
//            viewModel.callSaveTransaction()
        }

        binding.tvBenifits.movementMethod = LinkMovementMethod.getInstance()

        viewModel.userDatax.observe(this){
            if (it?.gender == "Woman") {
                binding.btnSkip.visible()
                isPlan = true
            }else{
                isPlan = false
                binding.btnSkip.gone()
            }
        }

        binding.btnSkip.setOnClickListener {
            openActivity<CongratulationsActivity>()
        }

        viewModel.stDateTime.set(viewModel.getCurrentDate())

    }

    private fun getPlanApi(){
        viewModel.callPlanApi()
    }

    private fun planObservers() {
        viewModel.response.observe(this) { response ->
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
                                viewModel.stProductID.set("plan_id_02")
                            }
                            setupRv()

                            val strToHtml = Html.fromHtml(this[0].cmsBody)
                            binding.tvBenifits.text = strToHtml
                            adapterPlan?.notifyDataSetChanged()
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
            toastInfo("billing Complete")
            viewModel.callSaveTransaction()
            }
    }

    override fun billingFailed() {
        runOnUiThread {
            toastInfo("billingFailed")
        }
    }

    override fun billingCanceled() {
        runOnUiThread {
            toastInfo("billing Canceled")
        }
    }

    override fun billingAlreadyPurchased() {
        runOnUiThread {
            toastInfo("billing Already Purchased")
        }
    }


    private fun planGetObservers() {
        viewModel.responseSave.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                   ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                   ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        openActivity<CongratulationsActivity>()
                        finish()
                        lifecycleScope.launch(Dispatchers.IO) {
                            userData?.isSubscribed = true
                            myDataStore.updateUser(userData!!)
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

    override fun onBackPressed() {
        super.onBackPressed()
//        finishAffinity()
    }

}