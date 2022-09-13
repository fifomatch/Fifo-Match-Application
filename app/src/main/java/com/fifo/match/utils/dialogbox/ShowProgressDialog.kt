package com.fifo.match.utils.dialogbox

import android.annotation.SuppressLint
import android.app.Activity
import android.view.WindowManager

object ShowProgressDialog {

    private var isLoadingVisible: Boolean = false

    @SuppressLint("StaticFieldLeak")
    private var mSRProgressDialog: SRProgressDialog? = null


    /**
     * Show progress dialog
     */
    fun showProgress(mActivity: Activity?) {
        try {
            if (isLoadingVisible) {
                hideProgress()
            }
            isLoadingVisible = true
            mSRProgressDialog = SRProgressDialog(mActivity)
            mSRProgressDialog?.setCancelable(false)
            mSRProgressDialog?.setCanceledOnTouchOutside(false)
            mSRProgressDialog?.show()
        } catch (ex: WindowManager.BadTokenException) {
            ex.printStackTrace()
        }
    }

    /**
     * method to hide progress
     */
    fun hideProgress() {
        try {
            if (mSRProgressDialog != null && mSRProgressDialog!!.isShowing) {
                mSRProgressDialog?.dismiss()
                isLoadingVisible = false
            }
        } catch (ex: WindowManager.BadTokenException) {
            ex.printStackTrace()
        }
    }
    fun isShowing()= isLoadingVisible
}
