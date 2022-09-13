package com.fifo.match.ui.activity.home.fragment.save

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.respository.Repository
import com.fifo.match.network.utils.Utility
import com.fifo.match.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveViewModel  @Inject constructor(private val apiRepository: Repository) : BaseViewModel(){

    val stCount = ObservableField("")
    val stStatus = ObservableField("")
    val stUserID = ObservableField("")

    private val _response: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val response: LiveData<NetworkResult<*>> = _response

    fun callAPI(){
        val map = mapOf("status" to stStatus.get()!!.toString(),
            )
        viewModelScope.launch {
            apiRepository.getSaveData(map).collect {
                _response.value = it
            }
        }
    }

    private val _responseRemove: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseSave: LiveData<NetworkResult<*>> = _responseRemove

    fun callRemoveUser(){
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "user_id" to stUserID.get()!!.toInt(),
                "status" to stStatus.get()!!.toString(),
            )
        )
        viewModelScope.launch {
            apiRepository.getRemoveUser(jsonObject!!).collect {
                _responseRemove.value = it
            }
        }
    }


    private val _responseLikeList: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseLikeList: LiveData<NetworkResult<*>> = _responseLikeList
    fun callLikeListAPI(){
        viewModelScope.launch {
            apiRepository.getLikeList().collect {
                _responseLikeList.value = it
            }
        }
    }


    private val _responseCreateChat: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseCreateChat: LiveData<NetworkResult<*>> = _responseCreateChat

    fun callCreateChat(){
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "user_id" to stUserID.get()!!.toInt(),
            )
        )
        viewModelScope.launch {
            apiRepository.createChat(jsonObject!!).collect {
                _responseCreateChat.value = it
            }
        }
    }
}