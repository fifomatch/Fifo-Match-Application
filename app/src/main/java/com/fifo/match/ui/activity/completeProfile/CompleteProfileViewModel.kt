package com.fifo.match.ui.activity.completeProfile

import android.util.Log
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
@HiltViewModel
class CompleteProfileViewModel @Inject constructor(private val apiRepository: Repository,private val myDataStore: MyDataStore) : BaseViewModel() {

    private val _response: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val response: LiveData<NetworkResult<*>> = _response

    private val _userData: MutableLiveData<SignupModel?> = MutableLiveData()
    val userData: LiveData<SignupModel?> = _userData

    init {
        getUserData()
    }

    fun getUserData(){
        viewModelScope.launch {
            _userData.value=myDataStore.getUser()
        }
    }

    fun getCompleteDataList(){
        viewModelScope.launch {
            apiRepository.getProfileDataList().collect {
                _response.value = it
            }
        }
    }


    val stEmail = ObservableField("")
    val stSwing = ObservableField("")
    val stBio = ObservableField("")
    val stRelationshipStatus = ObservableField("")
    val stOccupations = ObservableField("")
    val stEducation = ObservableField("")
    val stMinAge = ObservableField("")
    val stMaxAge = ObservableField("")
    val stWorkingFifo = ObservableField("")

    private val _responseFirst: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseFirst: LiveData<NetworkResult<*>> = _responseFirst

    fun getProfileFirstData(dob:String){
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "email" to stEmail.get().toString(),
                "dob" to dob,
                "about" to  stBio.get().toString(),
                "min_age" to stMinAge.get()!!.toDouble(),
                "max_age" to stMaxAge.get()!!.toDouble(),
                "relationship_status" to stRelationshipStatus.get()!!.toInt(),
                "occupation" to stOccupations.get()!!.toInt(),
                "education" to stEducation.get()!!.toInt(),
                "working_fifo" to stWorkingFifo.get().toString(),
                "swing" to stSwing.get().toString(),
            )
        )
        viewModelScope.launch {
            apiRepository.getProfileFirstData(jsonObject!!).collect {
                _responseFirst.value = it
            }
        }
    }


    private val _responsePhotos: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responsePhotos: LiveData<NetworkResult<*>> = _responsePhotos

    fun getProfilePhotos(imageFiles: List<File?>){
        val bodyGallery= arrayOfNulls<MultipartBody.Part>(imageFiles.size)
        if (imageFiles.isNotEmpty()) {
            for (i in imageFiles.indices) {
                val file = imageFiles[i]
                val requestImageFile = file!!.asRequestBody("image/*".toMediaTypeOrNull())
                bodyGallery[i] = MultipartBody.Part.createFormData("images[$i]", file.name, requestImageFile)
            }
        }
        viewModelScope.launch {
            apiRepository.getProfileSecondData(bodyGallery).collect {
                _responsePhotos.value = it
            }
        }
    }

    private val _responseDeletePhotos: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseDeletePhotos: LiveData<NetworkResult<*>> = _responseDeletePhotos

    fun getDeletePhotos(id: Int){
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "id" to id,
            )
        )
        viewModelScope.launch {
            apiRepository.getDeletePhoto(jsonObject!!).collect {
                _responseDeletePhotos.value = it
            }
        }
    }

}