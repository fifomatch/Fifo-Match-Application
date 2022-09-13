package com.fifo.match.ui.activity.membership

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fifo.match.local.MyDataStore
import com.fifo.match.model.SignupModel
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
class MembershipViewModel @Inject constructor(private val apiRepository: Repository,private val myDataStore: MyDataStore) : BaseViewModel() {

    private val _userDatax: MutableLiveData<SignupModel?> = MutableLiveData()
    val userDatax: LiveData<SignupModel?> = _userDatax

    init {
        getUserDatax()
    }

    fun getUserDatax(){
        viewModelScope.launch {
            _userDatax.value=myDataStore.getUser()
        }
    }

    private val _response: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val response: LiveData<NetworkResult<*>> = _response

    fun callPlanApi(){
        viewModelScope.launch {
            apiRepository.getPlanDetails().collect {
                _response.value = it
            }
        }
    }

    val stTransactionID = ObservableField("")
    val stProductID = ObservableField("")
    val stAmount = ObservableField("")
    val stDateTime = ObservableField("")

    private val _responseSave: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseSave: LiveData<NetworkResult<*>> = _responseSave

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
                _responseSave.value = it
            }
        }
    }

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        return sdf.format(Date())
    }

}