package com.fifo.match.inAppPurchase

import com.android.billingclient.api.Purchase

interface BillingCompleteListener {
    fun billingComplete(purchases: List<Purchase?>?)
    fun billingFailed()
    fun billingCanceled()
    fun billingAlreadyPurchased()
}