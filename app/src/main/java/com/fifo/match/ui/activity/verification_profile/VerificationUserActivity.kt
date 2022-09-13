package com.fifo.match.ui.activity.verification_profile

import com.fifo.match.R
import com.fifo.match.databinding.LayoutVerificationBinding
import com.fifo.match.network.utils.Constants
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.extensions.transparentStatusBar


class VerificationUserActivity : BaseActivity<VerificationViewModel, LayoutVerificationBinding>(VerificationViewModel::class.java) {

    var verify = ""

    override fun getLayoutRes() = R.layout.layout_verification

    override fun initView() {
        transparentStatusBar()
        binding.activity = this

        if (intent.hasExtra(Constants.PROFILE_VERIFY)){
            verify = intent.getStringExtra(Constants.PROFILE_VERIFY).toString()
        }

        if (verify == "1"){
            binding.textBelow.text = getString(R.string.you_have_successfully_submitted_your_verification_when_admin_aprove_your_request_then_update_you)
        }else if (verify == "2"){
            binding.textBelow.text = getString(R.string.you_are_verified_now)
        }
    }

}