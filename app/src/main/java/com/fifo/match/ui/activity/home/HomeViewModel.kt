package com.fifo.match.ui.activity.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fifo.match.local.MyDataStore
import com.fifo.match.model.SignupModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.respository.Repository
import com.fifo.match.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val apiRepository: Repository,private val myDataStore: MyDataStore) : BaseViewModel() {

    private val _response: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val response: LiveData<NetworkResult<*>> = _response

     fun callLiveUpdateApi() {
        viewModelScope.launch {
            apiRepository.getLiveUpdate().collect {
                _response.value = it
            }
        }

    }

    private val _userData: MutableLiveData<SignupModel?> = MutableLiveData()
    val userData: LiveData<SignupModel?> = _userData

    init {
        getUserDatax()
    }

    fun getUserDatax(){
        viewModelScope.launch {
            _userData.value=myDataStore.getUser()
        }
    }

    suspend fun clearSession() {
        myDataStore.clearSession()
    }
}