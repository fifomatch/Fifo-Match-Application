package com.fifo.match.ui.activity.survey

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fifo.match.model.SurveyModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.respository.Repository
import com.fifo.match.network.utils.Utility
import com.fifo.match.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SurveyViewModel @Inject constructor( private val apiRepository: Repository) : BaseViewModel() {

    val stHeightType = ObservableField("")
    val stHeightBar = ObservableField("")
    val stBodyType = ObservableField("")
    val stKids = ObservableField("")
    val stKidsInFuture = ObservableField("")
    val stSeeking = ObservableField("")
    val stPersonalityTypes = ObservableField("")
    val currentItem= ObservableField(0)
    val performIndex=ObservableField(0)


    private val _response: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val response: LiveData<NetworkResult<*>> = _response

    fun callSurveyFirstApi() {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "step" to 1,
                "height_type" to stHeightType.get().toString(),
                "height" to stHeightBar.get()!!.toDouble(),
            )
        )
        viewModelScope.launch {
            apiRepository.getCompleteSurvey(jsonObject!!).collect {
                _response.value = it
            }
        }
    }

    fun callSurveySecondApi() {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "step" to 2,
                "body_type" to stBodyType.get().toString(),
            )
        )
        viewModelScope.launch {
            apiRepository.getCompleteSurvey(jsonObject!!).collect {
                _response.value = it
            }
        }
    }

    fun callSurveyThirdApi() {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "step" to 3,
                "kids" to stKids.get().toString(),
                "kids_in_future" to stKidsInFuture.get().toString(),
            )
        )
        viewModelScope.launch {
            apiRepository.getCompleteSurvey(jsonObject!!).collect {
                _response.value = it
            }
        }
    }

    fun callSurveyFourApi() {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "step" to 4,
                "seeking" to stSeeking.get().toString()
            )
        )
        viewModelScope.launch {
            apiRepository.getCompleteSurvey(jsonObject!!).collect {
                _response.value = it
            }
        }
    }

    fun callSurveyFiveApi(list: HashSet<String>) {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "step" to 5,
                "my_qualities" to list.toString().replace("[","").replace("]","")
            )
        )
        viewModelScope.launch {
            apiRepository.getCompleteSurvey(jsonObject!!).collect {
                _response.value = it
            }
        }
    }

    fun callSurveySixApi(list: HashSet<String>) {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "step" to 6,
                "qualities_appreciate" to list.toString().replace("[","").replace("]","")
            )
        )
        viewModelScope.launch {
            apiRepository.getCompleteSurvey(jsonObject!!).collect {
                _response.value = it
            }
        }
    }

    fun callSurveySevenApi() {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "step" to 7,
                "personality_types" to stPersonalityTypes.get()!!.toString()
            )
        )
        viewModelScope.launch {
            apiRepository.getCompleteSurvey(jsonObject!!).collect {
                _response.value = it
            }
        }
    }

    val stSetData = MutableLiveData<SurveyModel>()
    private val _responseT: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseT: LiveData<NetworkResult<*>> = _responseT

    fun getSurveyDataApi() {
        viewModelScope.launch {
            apiRepository.getSurveyData().collect {
                _responseT.value = it
            }
        }
    }


    private val _responsePlan: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responsePlan: LiveData<NetworkResult<*>> = _responsePlan

    fun callActivePlan(){
        viewModelScope.launch {
            apiRepository.checkActivePlan().collect {
                _responsePlan.value = it
            }
        }
    }

}