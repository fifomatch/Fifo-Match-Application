package com.fifo.match.ui.activity.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.respository.Repository
import com.fifo.match.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel  @Inject constructor(private val apiRepository: Repository) : BaseViewModel(){


    private val _response: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val response: LiveData<NetworkResult<*>> = _response

     fun callNotificationApi() {
        viewModelScope.launch {
            apiRepository.getNotificationList().collect {
                _response.value = it
            }
        }

    }

    private val _responseClearNotification: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseClearNotification: LiveData<NetworkResult<*>> = _responseClearNotification

    fun callClearNotificationApi() {
        viewModelScope.launch {
            apiRepository.clearNotification().collect {
                _responseClearNotification.value = it
            }
        }

    }
}