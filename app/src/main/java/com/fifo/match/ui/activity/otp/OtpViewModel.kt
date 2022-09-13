package com.fifo.match.ui.activity.otp

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fifo.match.local.MyDataStore
import com.fifo.match.model.SignupModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.respository.Repository
import com.fifo.match.network.utils.Constants
import com.fifo.match.network.utils.Utility
import com.fifo.match.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(private val apiRepository: Repository, private val appDataStore: MyDataStore) : BaseViewModel() {

    /*****
     *   Register API
     ****/

    val stName = ObservableField("")
    val stMobileNumber = ObservableField("")
    val stCountryCode = ObservableField("")
    val stCountryName = ObservableField("")
    val stOtp = ObservableField("")
    val stGender = ObservableField("")
    val stInterested = ObservableField("")

    val stLatitude = ObservableField("")
    val stLongitude = ObservableField("")
    val stDeviceToken = ObservableField("")

    private val _response: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val response: LiveData<NetworkResult<*>> = _response

    fun callRegisterVerifyApi() {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "name" to stName.get().toString(),
                "mobile_number" to stMobileNumber.get().toString(),
                "otp" to stOtp.get().toString(),
                "gender" to stGender.get().toString(),
                "interested_in" to stInterested.get().toString(),
                "country_code" to stCountryCode.get().toString(),
                "country_name" to stCountryName.get().toString(),
                "device_token" to stDeviceToken.get().toString(),
                "app_version" to "v1.0",
                "os_version" to "1.1",
                "latitude" to stLatitude.get()!!.toDouble(),
                "longitude" to stLongitude.get()!!.toDouble(),
                "device_type" to "Android",
            )
        )
        viewModelScope.launch {
            apiRepository.callVerifyOtp(jsonObject!!).collect {
                _response.value = it
            }
        }
    }

    /***
     * SAVE DATA STORE
     * */
   suspend fun saveUser(signupBean: SignupModel) {
        appDataStore.startSession()
        appDataStore.saveUser(signupBean)
    }

    suspend fun putBoolean(boolean: Boolean) {
        appDataStore.putBoolean(Constants.checkLogin,boolean)

    }

    /****
     * Verify Login API
     * ***/

    fun callLoginVerifyApi() {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "mobile" to stMobileNumber.get().toString(),
                "password" to stOtp.get().toString(),
                "device_type" to "Android",
                "device_token" to stDeviceToken.get().toString(),
                "app_version" to "v1.0",
                "os_version" to "1.1",
                "latitude" to stLatitude.get()!!.toDouble(),
                "longitude" to stLongitude.get()!!.toDouble(),
                "country_code" to stCountryCode.get().toString()
            )
        )
        viewModelScope.launch {
            apiRepository.verifyLoginOTPApi(jsonObject!!).collect {
                _response.value = it
            }
        }
    }


    /**
     * Resend Otp Login
     * */

    private val _responseResend: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseResend: LiveData<NetworkResult<*>> = _responseResend


     fun callLoginApi() {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "mobile" to  stMobileNumber.get().toString(),
                "country_code" to  stCountryCode.get().toString()
            )
        )
        viewModelScope.launch {
            apiRepository.callLoginOTPApi(jsonObject!!).collect {
                _responseResend.value = it
            }
        }
    }

    fun callSignupApi() {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "mobile_number" to  stMobileNumber.get().toString(),
                "country_code" to  stCountryCode.get().toString()
            )
        )
        viewModelScope.launch {
            apiRepository.callSendOtp(jsonObject!!).collect {
                _responseResend.value = it
            }
        }
    }
}