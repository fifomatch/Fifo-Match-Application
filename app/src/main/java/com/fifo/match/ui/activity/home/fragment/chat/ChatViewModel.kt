package com.fifo.match.ui.activity.home.fragment.chat

import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.respository.Repository
import com.fifo.match.network.utils.Utility
import com.fifo.match.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val apiRepository: Repository) : BaseViewModel() {


    val stLatitude = ObservableField("")
    val stLongitude = ObservableField("")

    private val _responseUser: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseUser: LiveData<NetworkResult<*>> = _responseUser

    fun callUserData() {
        val map = mapOf(
            "user_id" to stUserID.get()!!.toInt(),
            "lat" to stLatitude.get()!!.toDouble().toString(),
            "lng" to stLongitude.get()!!.toDouble().toString(),
        )

        viewModelScope.launch {
            apiRepository.getUserData(map!! as Map<String, String>).collect {
                _responseUser.value = it
            }
        }
    }


    var chatListFound = MutableLiveData<Boolean>()

    val stUserID = ObservableField("")
    val stMessage = ObservableField("")

    private val _response: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val response: LiveData<NetworkResult<*>> = _response

     fun callSendNotificationApi() {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "user_id" to  stUserID.get()?.toInt().toString(),
                "message" to  stMessage.get().toString()
            )
        )

        viewModelScope.launch {
            apiRepository.sendChatNotification(jsonObject!!).collect {
                _response.value = it
            }
        }

    }

    private val _responseDeleteChat: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseDeleteChat: LiveData<NetworkResult<*>> = _responseDeleteChat



    fun callDeleteChatApi(stNodeID:String) {
        val jsonObject = Utility.convertToRequestBody(mapOf("node" to  stNodeID.toString()))
        viewModelScope.launch {
            apiRepository.getDeleteCHAT(jsonObject!!).collect {
                _responseDeleteChat.value = it
            }
        }
    }


}