package com.fifo.match.ui.activity.home.fragment.homeFragment

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fifo.match.local.MyDataStore
import com.fifo.match.model.MatchListModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.respository.Repository
import com.fifo.match.network.utils.Utility
import com.fifo.match.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeFragViewModel  @Inject constructor(private val apiRepository: Repository,private val appDataStore: MyDataStore) : BaseViewModel(){

    suspend fun clearSession() {
        appDataStore.clearSession()
    }

    val stType = ObservableField("")
    val stSetData = MutableLiveData<MatchListModel>()

    val stInterestedIn = ObservableField("")
    val stCountry = ObservableField("")
    val stOccupationID = ObservableField("")
    val stRelationshipID = ObservableField("")
    val stFifo = ObservableField("")
    val stDistance = ObservableField("")
    val stLat = ObservableField("")
    val stLong = ObservableField("")
    val stHeight = ObservableField("")
    val stHeightType = ObservableField("")
    val stMinAge = ObservableField("")
    val stMaxAge = ObservableField("")
    val stBodyType = ObservableField("")

    private val _responseT: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseTT: LiveData<NetworkResult<*>> = _responseT

    fun getMatchListApi() {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "interested_in" to stInterestedIn.get().toString(),
                "country" to stCountry.get().toString(),
                "occupation" to stOccupationID.get().toString(),
                "relationship_status" to stRelationshipID.get().toString(),
                "fifo" to stFifo.get().toString(),
                "lat" to stLat.get().toString(),
                "lng" to stLong.get().toString(),
                "distance" to stDistance.get().toString(),
                "height" to stHeight.get().toString(),
                "height_type" to stHeightType.get().toString(),
                "min_age" to stMinAge.get().toString(),
                "max_age" to stMaxAge.get().toString(),
                "body_type" to stBodyType.get().toString(),
            )
        )
        viewModelScope.launch {
            apiRepository.getMatchList(jsonObject!!).collect {
                _responseT.value = it
            }
        }
    }

    val stUserID = ObservableField("")
    val stStatus = ObservableField("")

    private val _responseS: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseLike: LiveData<NetworkResult<*>> = _responseS

    fun callSaveLikeCancel(){
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

    fun callUserLike(){
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

    private val _responseSpinner: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseSpinner: LiveData<NetworkResult<*>> = _responseSpinner


    fun getCompleteDataList(){
        viewModelScope.launch {
            apiRepository.getProfileDataList().collect {
                _responseSpinner.value = it
            }
        }
    }

}