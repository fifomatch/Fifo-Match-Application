package com.fifo.match.ui.activity.chat

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

abstract class BaseBindingActivity : AppCompatActivity() {

    protected var mActivity: AppCompatActivity? = null

    protected var toolBar: Toolbar? = null

    protected var fragment: Fragment? = null

    protected abstract fun createContentView()

    protected abstract fun createActivityObject()

    protected abstract fun initializeObject()
    protected abstract fun setOnClickListener()


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createActivityObject()
        createContentView()
        initializeObject()
        setOnClickListener()

    }

    override fun onResume() {
        super.onResume()
//        if (mActivity == null) throw NullPointerException("must create activty object")


    }

    override fun onPause() {
        super.onPause()
    }

//    fun changeFragment(fragment: Fragment, isAddToBack: Boolean) {
//        this.fragment = fragment
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.container, fragment, fragment.javaClass.name)
//        if (isAddToBack) transaction.addToBackStack(fragment.javaClass.name)
//        transaction.commit()
//    }

}

