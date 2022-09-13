package com.fifo.match.ui.activity.removeAccount

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.fifo.match.R
import com.fifo.match.databinding.ActivityRemoveAccountBinding
import com.fifo.match.databinding.BottomsheetLogoutBinding
import com.fifo.match.network.NetworkResult
import com.fifo.match.ui.activity.home.fragment.profile.ProfileViewModel
import com.fifo.match.ui.activity.welcome.WelcomeActivity
import com.fifo.match.ui.base.BaseActivity
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import com.github.hariprasanths.bounceview.BounceView
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RemoveAccountActivity : BaseActivity<ProfileViewModel, ActivityRemoveAccountBinding>(ProfileViewModel::class.java) {

    override fun getLayoutRes() = R.layout.activity_remove_account
    private val viewModel by viewModels<ProfileViewModel>()

    override fun initView() {
        transparentStatusBar()
        binding.activity = this
        getSignOutObservers()
    }

    fun onNext(type: Int) {
        when (type) {
            1 -> {
                bottomSheetLogOut(1, "Are you sure you want to deactivate account")
            }
            2 -> {
                bottomSheetLogOut(2, "Are you sure you want to delete account")
            }
        }
    }

    private fun bottomSheetLogOut(type: Int, message: String) {
        val binding: BottomsheetLogoutBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottomsheet_logout, null, false)
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogThemeXX)
        BounceView.addAnimTo(dialog);
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        binding.btnLogout.roundBorderedViewFromResId(15, R.color.white, R.color.white, 1)
        binding.btnCancel.roundBorderedViewFromResId(15, R.color.transperent, R.color.white, 1)

        binding.tvMessage.text = message

        binding.btnLogout.setOnClickListener {
            if (type == 1) {
                viewModel.callDeactivateAccountAPI()
            } else {
                viewModel.stSignOut.set("1")
                viewModel.callSignOutApi()
            }
            dialog.dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(binding.root)
        dialog.show()
    }

    private fun getSignOutObservers() {
        viewModel.responseSignOut.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    response.data.let {
                        openActivity<WelcomeActivity>()
                        lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.clearSession()
                        }
                        finish()
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