package com.fifo.match.inAppPurchase


import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InAppBilling(private val mContext: Context, private val billingCompleteListener: BillingCompleteListener) : PurchasesUpdatedListener {

    private val TAG="InAppBilling"
    private var billingClient: BillingClient? = null
    private var PackageType: String? = null

     fun initializeBillingClient(purchaseCallBack:(Boolean)->Unit) {
        billingClient = BillingClient.newBuilder(mContext).enablePendingPurchases().setListener(this)
            .build()
             billingClient!!.startConnection(object : BillingClientStateListener {
                 override fun onBillingSetupFinished(billingResult: BillingResult) {
                     purchaseCallBack.invoke(true)
                 }
                 override fun onBillingServiceDisconnected() {
                     purchaseCallBack.invoke(false)
                 }
             })
    }

    fun checkSubscription(isSubs:(Boolean)->Unit){
        billingClient!!.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS) { _, p1 ->
            if (p1 != null) {
                isSubs.invoke(p1.isNotEmpty())
            } else
                isSubs.invoke(false)
        }
    }

    fun addSkuList(PackageType: String?) {
        this.PackageType = PackageType
        val skuList: MutableList<String?> = ArrayList()
        skuList.add(PackageType)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
        billingClient!!.querySkuDetailsAsync(
            params.build()) { billingResult, list ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
                Log.d("lets", "querySkuDetailsAsync, responseCode: \$responseCode $list")
                if (list.size > 0) onClickPurchaseItem(list[0])
                for (skuDetails in list) {
                    val sku = skuDetails.sku
                    val price = skuDetails.price
                    Log.e("price", price)
                }
            }
        }
    }

    fun addAllSkuList(skuList:List<String>, purchaseType: PurchaseType, listOfProductOrSubs:(MutableList<SkuDetails>)->Unit) {
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(if(purchaseType== PurchaseType.PRODUCT) BillingClient.SkuType.INAPP else BillingClient.SkuType.SUBS)
        billingClient!!.querySkuDetailsAsync(
            params.build()) { billingResult, list ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
                listOfProductOrSubs.invoke(list)
            }
            else
                listOfProductOrSubs.invoke(mutableListOf())
        }
    }

    private fun onClickPurchaseItem(skuDetails: SkuDetails) {
        val builder = BillingFlowParams
            .newBuilder() //                .setSku(skuDetails).setType(BillingClient.SkuType.INAPP)
            .setSkuDetails(skuDetails)
        billingClient!!.launchBillingFlow((mContext as Activity), builder.build())
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        Log.e("onPurchasesUpdated",""+billingResult.responseCode)
        CoroutineScope(Dispatchers.Main).launch {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !purchases.isNullOrEmpty()) {
                val purchase=purchases[0]
                if(!purchase.isAcknowledged)
                {
                    if(purchase.purchaseState==Purchase.PurchaseState.PURCHASED)
                        handlePurchase(purchase)
                }
                billingCompleteListener.billingComplete(purchases)
                /*val sharedPref= SharedPreferenceUtility.getInstance()
                val isPurchased= sharedPref.get<Boolean?>(SharedPreferenceUtility.IS_PURCHASED)?:false
                val purchase=purchases[0]
                if (!isPurchased) {
                    sharedPref.save(SharedPreferenceUtility.IS_PURCHASED,true)

                    billingCompleteListener.billingComplete(purchases)
                }
                if(!purchase.isAcknowledged)
                {
                    if(purchase.purchaseState==Purchase.PurchaseState.PURCHASED)
                        handlePurchase(purchase)
                }*/

            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                billingCompleteListener.billingCanceled()
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                // Handle an error caused by a user cancelling the purchase flow.

                billingCompleteListener.billingAlreadyPurchased()
                val purchaseToken = "inapp:" + "android.test.purchased"
            } else {
                billingCompleteListener.billingFailed()
                // Handle any other error codes.
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
            val acknowledgePurchaseParams: AcknowledgePurchaseParams =
                AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
            billingClient!!.acknowledgePurchase(acknowledgePurchaseParams
            ) { billingResult ->
                Log.e(TAG,billingResult.debugMessage)
               /* val consumeParams =
                    ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                billingClient!!.consumeAsync(consumeParams
                ) { p0, p1 ->
                    Log.e(TAG,"$p0 #### $p1")
//                    billingClient?.endConnection()
                }*/
            }

    }
}

/*
annotation class BillingResponseCode {
    companion object {
        var SERVICE_TIMEOUT = -3
        var FEATURE_NOT_SUPPORTED = -2
        var SERVICE_DISCONNECTED = -1
        var OK = 0
        var USER_CANCELED = 1
        var SERVICE_UNAVAILABLE = 2
        var BILLING_UNAVAILABLE = 3
        var ITEM_UNAVAILABLE = 4
        var DEVELOPER_ERROR = 5
        var ERROR = 6
        var ITEM_ALREADY_OWNED = 7
        var ITEM_NOT_OWNED = 8
    }
}*/
