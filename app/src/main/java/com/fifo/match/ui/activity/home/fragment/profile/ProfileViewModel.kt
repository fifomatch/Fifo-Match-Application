package com.fifo.match.ui.activity.home.fragment.profile

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fifo.match.local.MyDataStore
import com.fifo.match.model.CheckPlanModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.respository.Repository
import com.fifo.match.network.utils.Utility
import com.fifo.match.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val apiRepository: Repository ,private val appDataStore: MyDataStore) : BaseViewModel() {

    val stCheckPlan = MutableLiveData<CheckPlanModel>()

    private val _responsePlan: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responsePlan: LiveData<NetworkResult<*>> = _responsePlan

    fun callActivePlan(){
        viewModelScope.launch {
            apiRepository.checkActivePlan().collect {
                _responsePlan.value = it
            }
        }
    }

    val stStatus = ObservableField("")
    val stUserID = ObservableField("")

    private val _responseHideProfile: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseHideProfile: LiveData<NetworkResult<*>> = _responseHideProfile

    fun callHiddenProfileAPI(){
        val map = mapOf("status" to stStatus.get()!!.toString(),
        )
        viewModelScope.launch {
            apiRepository.getSaveData(map).collect {
                _responseHideProfile.value = it
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

     suspend fun clearSession() {
        appDataStore.clearSession()
    }

    private val _responseMyProfile: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseMyProfile: LiveData<NetworkResult<*>> = _responseMyProfile

    fun getMyProfile(){
        viewModelScope.launch {
            apiRepository.getMyProfile().collect {
                _responseMyProfile.value = it
            }
        }
    }

    val stOnlineOffline = ObservableField("")
    val stNotificationStatus = ObservableField("")

    private val _responseOnline: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseOnline: LiveData<NetworkResult<*>> = _responseOnline

    fun callOnlineOffline(){
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "status" to stOnlineOffline.get()!!.toString(),
            )
        )
        viewModelScope.launch {
            apiRepository.getOnlineOffline(jsonObject!!).collect {
                _responseOnline.value = it
            }
        }
    }

    fun callNotificationStatus(){
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "status" to stNotificationStatus.get()!!.toString(),
            )
        )
        viewModelScope.launch {
            apiRepository.getNotification(jsonObject!!).collect {
                _responseOnline.value = it
            }
        }
    }


    private val _responseAppearance: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseAppearance: LiveData<NetworkResult<*>> = _responseAppearance

    fun callAppearanceAPI(){
        viewModelScope.launch {
            apiRepository.getApperance().collect {
                _responseAppearance.value = it
            }
        }
    }

    val stSignOut = ObservableField("")

    private val _responseSignOut: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseSignOut: LiveData<NetworkResult<*>> = _responseSignOut

    fun callSignOutApi(){
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "is_delete" to stSignOut.get()!!.toInt().toString()
            )
        )
        viewModelScope.launch {
            apiRepository.signOut(jsonObject!!).collect {
                _responseSignOut.value = it
            }
        }
    }

    fun callDeactivateAccountAPI(){
        viewModelScope.launch {
            apiRepository.deactivateAccount().collect {
                _responseSignOut.value = it
            }
        }
    }


    private val _responsePlanDetails: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responsePlanDetails: LiveData<NetworkResult<*>> = _responsePlanDetails

    fun callPlanApi(){
        viewModelScope.launch {
            apiRepository.getPlanDetails().collect {
                _responsePlanDetails.value = it
            }
        }
    }

    val stTransactionID = ObservableField("")
    val stProductID = ObservableField("")
    val stAmount = ObservableField("")
    val stDateTime = ObservableField("")

    private val _responsePurchasePlan: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responsePurchasePlan: LiveData<NetworkResult<*>> = _responsePurchasePlan

    fun callSaveTransaction(){
        val time = System.currentTimeMillis().toString()
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "transaction_id" to time, //stTransactionID.get().toString(),
                "type" to "google",
                "product_id" to stProductID.get().toString(),
                "amount" to stAmount.get()!!.substring(1).toDouble(),
                "transaction_datetime" to stDateTime.get().toString(),
                "membership_type" to "month",
                "membership_type_value" to 30,
            )
        )
        viewModelScope.launch {
            apiRepository.getSaveTransaction(jsonObject!!).collect {
                _responsePurchasePlan.value = it
            }
        }
    }

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        return sdf.format(Date())
    }
}