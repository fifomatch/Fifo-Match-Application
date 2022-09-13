package com.fifo.match.ui.activity.view

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fifo.match.model.CheckPlanModel
import com.fifo.match.model.UserDetailsModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.respository.Repository
import com.fifo.match.network.utils.Utility
import com.fifo.match.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewViewModel @Inject constructor(private val apiRepository: Repository) : BaseViewModel() {

    val stSetData = MutableLiveData<UserDetailsModel>()

    val stUserID = ObservableField("")
    val stLatitude = ObservableField("")
    val stLongitude = ObservableField("")

    private val _responseUser: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseUser: LiveData<NetworkResult<*>> = _responseUser

    fun callUserData() {
        val map = mapOf(
            "user_id" to stUserID.get()!!.toInt().toString(),
            "lat" to stLatitude.get()!!.toDouble().toString(),
            "lng" to stLongitude.get()!!.toDouble().toString(),
        )

        viewModelScope.launch {
            apiRepository.getUserData(map!!).collect {
                _responseUser.value = it
            }
        }
    }

    val stCheckPlan = MutableLiveData<CheckPlanModel>()

    private val _responsePlan: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responsePlan: LiveData<NetworkResult<*>> = _responsePlan

    fun callActivePlan() {
        viewModelScope.launch {
            apiRepository.checkActivePlan().collect {
                _responsePlan.value = it
            }
        }
    }

    val stStatus = ObservableField("")
    private val _responseS: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseLike: LiveData<NetworkResult<*>> = _responseS

    fun callSaveLikeCancel() {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "user_id" to stUserID.get().toString(),
                "status" to stStatus.get()!!.toString(),
            )
        )
        viewModelScope.launch {
            apiRepository.getSaveLikeCancel(jsonObject!!).collect {
                _responseS.value = it
            }
        }
    }

    fun callRemoveUser() {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "user_id" to stUserID.get()!!.toInt(),
                "status" to stStatus.get()!!.toString(),
            )
        )
        viewModelScope.launch {
            apiRepository.getRemoveUser(jsonObject!!).collect {
                _responseS.value = it
            }
        }
    }

    fun callUserLike() {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "user_id" to stUserID.get().toString(),
            )
        )
        viewModelScope.launch {
            apiRepository.getUserLike(jsonObject!!).collect {
                _responseS.value = it
            }
        }
    }

    fun callUserDislike() {
        val map = mapOf(
            "user_id" to stUserID.get().toString(),
        )
        viewModelScope.launch {
            apiRepository.getUserDislike(map).collect {
                _responseS.value = it
            }
        }
    }

}