package com.fifo.match.ui.base

import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.fifo.match.App
import com.fifo.match.model.SignupModel
import com.fifo.match.utils.extensions.toastError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


abstract class BaseActivity<VM : BaseViewModel, DB : ViewDataBinding>(private val mViewModelClass: Class<VM>) : AppCompatActivity() {

    @LayoutRes
    abstract fun getLayoutRes(): Int

    val binding by lazy {
        DataBindingUtil.setContentView(this, getLayoutRes()) as DB
    }

    var userData:SignupModel?=null

    /**
     * If you want to inject Dependency Injection
     * on your activity, you can override this.
     */

    open fun onInject(){}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            userData = (applicationContext as App).appDataStore.getUser()
            withContext(Dispatchers.Main) {
                initView()
                binding.lifecycleOwner = this@BaseActivity
                attachBaseObserver()
            }
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE)

    }

    abstract fun initView()

    private fun attachBaseObserver() {}

    fun showErrorMessage(message: String) {
        toastError(message)
    }



}