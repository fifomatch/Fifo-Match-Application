package com.fifo.match.ui.activity.match

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
class MatchViewModel @Inject constructor(private val apiRepository: Repository) : BaseViewModel() {

    val stId = ObservableField("")

    private val _response: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val response: LiveData<NetworkResult<*>> = _response

    fun callAPI() {
        val map = mapOf(
            "id" to stId.get()!!.toInt().toString(),
        )
        viewModelScope.launch {
            apiRepository.getLikeDetails(map).collect {
                _response.value = it
            }
        }
    }

    val stUserID = ObservableField("")

    private val _responseCreateChat: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseCreateChat: LiveData<NetworkResult<*>> = _responseCreateChat

    fun callCreateChat() {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "user_id" to stUserID.get()?.toInt().toString(),
            )
        )
        viewModelScope.launch {
            apiRepository.createChat(jsonObject!!).collect {
                _responseCreateChat.value = it
            }
        }
    }

}