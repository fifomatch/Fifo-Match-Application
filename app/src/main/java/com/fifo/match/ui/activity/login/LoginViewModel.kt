package com.fifo.match.ui.activity.login

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fifo.match.R
import com.fifo.match.ui.base.BaseViewModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.respository.Repository
import com.fifo.match.network.utils.Utility
import com.fifo.match.utils.extensions.toastError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val apiRepository: Repository) : BaseViewModel() {

    val stMobileNumber = ObservableField("")
    val stCountryCode = ObservableField("")

    private val _response: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val response: LiveData<NetworkResult<*>> = _response

    fun onNext(view: View) {
          if (stMobileNumber.get().isNullOrEmpty()) {
              view.context.toastError(view.context.getString(R.string.enter_phone_number))
          } else if (stMobileNumber.get()!!.length < 4 || stMobileNumber.get()!!.length >= 13) {
              view.context.toastError(view.context.getString(R.string.phone_number_valid))
          } else {
              callLoginApi()
          }
    }

    private fun callLoginApi() {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "mobile" to  stMobileNumber.get().toString(),
                "country_code" to  stCountryCode.get().toString()
            )
        )
        viewModelScope.launch {
            apiRepository.callLoginOTPApi(jsonObject!!).collect {
                _response.value = it
            }
        }
    }


}