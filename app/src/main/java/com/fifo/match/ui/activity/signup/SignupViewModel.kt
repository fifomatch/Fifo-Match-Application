package com.fifo.match.ui.activity.signup

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fifo.match.ui.base.BaseViewModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.respository.Repository
import com.fifo.match.network.utils.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SignupViewModel @Inject constructor(private val apiRepository: Repository) : BaseViewModel() {

    val stName   = ObservableField("")
    val stMobileNumber = ObservableField("")
    val stCountryCode = ObservableField("")

    private val _response: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val response: LiveData<NetworkResult<*>> = _response

     fun callSignupApi() {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "mobile_number" to  stMobileNumber.get().toString(),
                "country_code" to  stCountryCode.get().toString()
            )
        )
        viewModelScope.launch {
            apiRepository.callSendOtp(jsonObject!!).collect {
                _response.value = it
            }
        }
    }

}