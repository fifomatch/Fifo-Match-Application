package com.fifo.match.ui.activity.verification_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.respository.Repository
import com.fifo.match.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(private val apiRepository: Repository) : BaseViewModel() {

    private val _response: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val response: LiveData<NetworkResult<*>> = _response

     fun callUserVerifyApi(filePhoto: File) {
         val builder = MultipartBody.Builder()
         builder.setType(MultipartBody.FORM)
         builder.addFormDataPart("photo", filePhoto!!.name, filePhoto.asRequestBody("/*".toMediaTypeOrNull()))
         val requestBody = builder.build()
        viewModelScope.launch {
            apiRepository.userVerification(requestBody).collect {
                _response.value = it
            }
        }

    }
}