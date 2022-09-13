package com.fifo.match.ui.activity.congratulations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.respository.Repository
import com.fifo.match.network.utils.Utility
import com.fifo.match.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CongratulationsViewModel @Inject constructor(private val apiRepository: Repository) : BaseViewModel() {


    private val _responsePlan: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responsePlan: LiveData<NetworkResult<*>> = _responsePlan

    fun callActivePlan(){
        viewModelScope.launch {
            apiRepository.checkActivePlan().collect {
                _responsePlan.value = it
            }
        }
    }


    private val _responseSurvey: MutableLiveData<NetworkResult<*>> = MutableLiveData()
    val responseSurvey: LiveData<NetworkResult<*>> = _responseSurvey

    fun callSurveySevenApi() {
        val jsonObject = Utility.convertToRequestBody(
            mapOf(
                "step" to 7,
                "personality_types" to "7"
            )
        )
        viewModelScope.launch {
            apiRepository.getCompleteSurvey(jsonObject!!).collect {
                _responseSurvey.value = it
            }
        }
    }

}