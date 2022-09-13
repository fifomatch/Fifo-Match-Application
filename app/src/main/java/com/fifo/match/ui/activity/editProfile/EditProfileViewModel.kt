package com.fifo.match.ui.activity.editProfile

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.respository.Repository
import com.fifo.match.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject


@HiltViewModel
class EditProfileViewModel@Inject constructor(private val apiRepository: Repository) : BaseViewModel() {

    var stEditProfile = ObservableBoolean(false)

    val stRelationshipStatus = ObservableField("")
    val stOccupations = ObservableField("")
    val stEducation = ObservableField("")
    val userProfilePhoto = ArrayList<MultipartBody.Part>()
    val stName =  ObservableField("")
    val stGender =  ObservableField("")
    val stInterested =  ObservableField("")
    val stDOB =  ObservableField("")

    private val _response: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val response: LiveData<NetworkResult<*>> = _response

    fun getSpinnerDataList(){
        viewModelScope.launch {
            apiRepository.getProfileDataList().collect {
                _response.value = it
            }
        }
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


    private val _responseUpdateProfile: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseUpdateProfile: LiveData<NetworkResult<*>> = _responseUpdateProfile

    fun updateProfile(){
        val request: LinkedHashMap<String, RequestBody> = LinkedHashMap()
        request["name"] = stName.get().toString().toRequestBody()
        request["gender"] = stGender.get().toString().toRequestBody()
        request["interested_in"] = stInterested.get().toString().toRequestBody()
        request["dob"] = stDOB.get().toString().toRequestBody()
        request["relationship_status_id"] = stRelationshipStatus.get()!!.toInt().toString().toRequestBody()
        request["occupation_id"] = stOccupations.get()!!.toInt().toString().toRequestBody()
        request["education_id"] = stEducation.get()!!.toInt().toString().toRequestBody()

        val documents = ArrayList<MultipartBody.Part>()
        documents.addAll(userProfilePhoto)

        viewModelScope.launch {
            apiRepository.updateProfile(request,documents).collect {
                _responseUpdateProfile.value = it
            }
        }

    }

}