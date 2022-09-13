package com.fifo.match.ui.activity.splash

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import com.fifo.match.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class SplashViewModel  @Inject constructor() : BaseViewModel(){

    val isFinished = MutableLiveData<Boolean>(false)

    private val timer = object : CountDownTimer(3000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            isFinished.postValue(true);
        }

    }

    fun start() {
        timer.start()
    }

}