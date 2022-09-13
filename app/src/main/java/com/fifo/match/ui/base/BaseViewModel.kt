package com.fifo.match.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel(),CoroutineScope {

    val exceptionLiveData: MutableLiveData<Throwable> = MutableLiveData()

    var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job


    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }



}
