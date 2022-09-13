package com.fifo.match.network.respository

import com.fifo.match.local.MyDataStore
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.api.ApiService
import com.fifo.match.network.api.BaseApiResponse
import com.fifo.match.network.utils.Constants
import com.fifo.match.network.utils.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class Repository @Inject constructor(
    private val apiService: ApiService,
    private val appDataStore: MyDataStore
) : BaseApiResponse() {

    private suspend fun callProfileApi(
        endPoint: String,
        requestBody: LinkedHashMap<String, RequestBody>,
        imageFile: List<MultipartBody.Part>
    ): Flow<NetworkResult<*>> {
        return flow<NetworkResult<*>> {
            val signupModel = appDataStore.getUser()
            emit(NetworkResult.Loading)
            when (endPoint) {
                Constants.MY_PROFILE -> {
                    emit(safeApiCall {
                        apiService.updateProfile(
                            requestBody,
                            imageFile,
                            "Bearer " + signupModel?.authToken ?: ""
                        )
                    })
                }

            }
        }
    }

    suspend fun updateProfile(
        requestBody: LinkedHashMap<String, RequestBody>,
        imageFile: List<MultipartBody.Part>
    ) = callProfileApi(Constants.MY_PROFILE, requestBody, imageFile)


    private suspend fun callGetApi(
        endPoint: String,
        params: Map<String, String>
    ): Flow<NetworkResult<*>> {
        return flow<NetworkResult<*>> {
            val signupModel = appDataStore.getUser()
            emit(NetworkResult.Loading)
            when (endPoint) {
                Constants.USER_DETAILS -> {
                    emit(safeApiCall {
                        apiService.getUserData(params, "Bearer " + signupModel?.authToken ?: "")
                    })
                }
                Constants.SAVE_LIST -> {
                    emit(safeApiCall {
                        apiService.getSaveData(params, "Bearer " + signupModel?.authToken ?: "")
                    })
                }
                Constants.USER_DISLIKE -> {
                    emit(safeApiCall {
                        apiService.getUserDislike(params, "Bearer " + signupModel?.authToken ?: "")
                    })
                }
                Constants.LIKE_DETAILS -> {
                    emit(safeApiCall {
                        apiService.getLikeDetails(params, "Bearer " + signupModel?.authToken ?: "")
                    })
                }
            }
        }
    }


    private suspend fun callApiPhotos(
        endPoint: String,
        imageFile: Array<MultipartBody.Part?>
    ): Flow<NetworkResult<*>> {
        return flow<NetworkResult<*>> {
            val signupModel = appDataStore.getUser()
            emit(NetworkResult.Loading)
            when (endPoint) {
                Constants.COMPLETE_PROFILE_SECOND -> {
                    emit(safeApiCall {
                        apiService.getProfileSecondData(
                            imageFile,
                            "Bearer " + signupModel?.authToken ?: ""
                        )
                    })
                }

            }
        }
    }

    private suspend fun callApi(
        endPoint: String,
        requestBody: RequestBody
    ): Flow<NetworkResult<*>> {
        return flow<NetworkResult<*>> {
            emit(NetworkResult.Loading)
            val signupModel = appDataStore.getUser()
            when (endPoint) {
                Constants.LOGIN_OTP_API -> {
                    emit(safeApiCall {
                        apiService.loginOTPApi(requestBody)
                    })
                }
                Constants.LOGIN_API -> {
                    emit(safeApiCall {
                        apiService.verifyLoginOTPApi(requestBody)
                    })
                }
                Constants.SEND_OTP -> {
                    emit(safeApiCall {
                        apiService.sendOtp(requestBody)
                    })
                }
                Constants.VERIFY_OTP -> {
                    emit(safeApiCall {
                        apiService.verifyOtp(requestBody)
                    })
                }
                Constants.PROFILE_LIST_DATA -> {
                    emit(safeApiCall {
                        apiService.getProfileData()
                    })
                }
                Constants.COMPLETE_PROFILE_FIRST -> {
                    emit(safeApiCall {
                        apiService.getProfileFirstData(
                            requestBody,
                            "Bearer " + signupModel?.authToken
                        )
                    })
                }
                Constants.COMPLETE_SURVERY_FIRST -> {
                    emit(safeApiCall {
                        apiService.getCompleteSurvey(
                            requestBody,
                            "Bearer " + signupModel?.authToken
                        )
                    })
                }
                Constants.PLAN_DETAILS -> {
                    emit(safeApiCall {
                        apiService.getPlanDetails("Bearer " + signupModel?.authToken)
                    })
                }
                Constants.LIVE_UPDATE -> {
                    emit(safeApiCall {
                        apiService.getLiveUpdate("Bearer " + signupModel?.authToken)
                    })
                }
                Constants.SAVE_TRANSACTION -> {
                    emit(safeApiCall {
                        apiService.getSaveTransaction(
                            requestBody,
                            "Bearer " + signupModel?.authToken
                        )
                    })
                }
                Constants.SURVEY_SETUP -> {
                    emit(safeApiCall {
                        apiService.getSurveyData("Bearer " + signupModel?.authToken)
                    })
                }
                Constants.MATCH_LIST -> {
                    emit(safeApiCall {
                        apiService.getMatchList(requestBody,"Bearer " + signupModel?.authToken)
                    })
                }
                Constants.USER_SAVE_Like -> {
                    emit(safeApiCall {
                        apiService.getSaveLikeCancel(
                            requestBody,
                            "Bearer " + signupModel?.authToken
                        )
                    })
                }
                Constants.REMOVE_USER -> {
                    emit(safeApiCall {
                        apiService.getRemoveUser(requestBody, "Bearer " + signupModel?.authToken)
                    })
                }
                Constants.CHECK_ACTIVE_PLAN -> {
                    emit(safeApiCall {
                        apiService.checkActivePlan("Bearer " + signupModel?.authToken)
                    })
                }
                Constants.MY_PROFILE -> {
                    emit(safeApiCall {
                        apiService.getMyProfile("Bearer " + signupModel?.authToken)
                    })
                }
                Constants.ONLINE_OFFLINE -> {
                    emit(safeApiCall {
                        apiService.getOnlineOffline(requestBody, "Bearer " + signupModel?.authToken)
                    })
                }
                Constants.NOTIFICATION_STATUS -> {
                    emit(safeApiCall {
                        apiService.getNotification(requestBody, "Bearer " + signupModel?.authToken)
                    })
                }
                Constants.NOTIFICATION_LIST -> {
                    emit(safeApiCall {
                        apiService.getNotificationList("Bearer " + signupModel?.authToken)
                    })
                }
                Constants.USER_LIKE -> {
                    emit(safeApiCall {
                        apiService.getUserLike(requestBody, "Bearer " + signupModel?.authToken)
                    })
                }
                Constants.LIKE_LIST -> {
                    emit(safeApiCall {
                        apiService.getLikeList("Bearer " + signupModel?.authToken)
                    })
                }
                Constants.APPEARENCE -> {
                    emit(safeApiCall {
                        apiService.getAppearance("Bearer " + signupModel?.authToken)
                    })
                }
                Constants.CREATE_CHAT -> {
                    emit(safeApiCall {
                        apiService.createChat(requestBody, "Bearer " + signupModel?.authToken)
                    })
                }
                Constants.CLEAR_NOTIFICATION -> {
                    emit(safeApiCall {
                        apiService.clearNotification("Bearer " + signupModel?.authToken)
                    })
                }
                Constants.SEND_CHAT_NOTIFICATION -> {
                    emit(safeApiCall {
                        apiService.sendChatNotification(requestBody, "Bearer " + signupModel?.authToken)
                    })
                }
                Constants.USER_VERIFICATION -> {
                    emit(safeApiCall {
                        apiService.userVerification(requestBody, "Bearer " + signupModel?.authToken)
                    })
                }
                Constants.SIGN_OUT -> {
                    emit(safeApiCall {
                        apiService.signOut(requestBody, "Bearer " + signupModel?.authToken)
                    })
                }
                Constants.DEACTIVATE_ACCOUNT -> {
                    emit(safeApiCall {
                        apiService.deactivateAccount(requestBody, "Bearer " + signupModel?.authToken)
                    })
                }
                Constants.DELETE_PHOTO -> {
                    emit(safeApiCall {
                        apiService.getDeletePhoto(requestBody, "Bearer " + signupModel?.authToken)
                    })
                }
                Constants.DELETE_CHAT -> {
                    emit(safeApiCall {
                        apiService.getDeleteCHAT(requestBody, "Bearer " + signupModel?.authToken)
                    })
                }

            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun signOut(requestBody: RequestBody) = callApi(Constants.SIGN_OUT, requestBody)

    suspend fun getDeletePhoto(requestBody: RequestBody) = callApi(Constants.DELETE_PHOTO, requestBody)

    suspend fun getDeleteCHAT(requestBody: RequestBody) = callApi(Constants.DELETE_CHAT, requestBody)

    suspend fun userVerification(requestBody: RequestBody) = callApi(Constants.USER_VERIFICATION, requestBody)

    suspend fun getUserLike(requestBody: RequestBody) = callApi(Constants.USER_LIKE, requestBody)

    suspend fun sendChatNotification(requestBody: RequestBody) = callApi(Constants.SEND_CHAT_NOTIFICATION, requestBody)

    suspend fun createChat(requestBody: RequestBody) = callApi(Constants.CREATE_CHAT, requestBody)

    suspend fun getNotification(requestBody: RequestBody) =
        callApi(Constants.NOTIFICATION_STATUS, requestBody)

    suspend fun getOnlineOffline(requestBody: RequestBody) =
        callApi(Constants.ONLINE_OFFLINE, requestBody)

    suspend fun callLoginOTPApi(requestBody: RequestBody) =
        callApi(Constants.LOGIN_OTP_API, requestBody)

    suspend fun getSaveLikeCancel(requestBody: RequestBody) =
        callApi(Constants.USER_SAVE_Like, requestBody)

    suspend fun verifyLoginOTPApi(requestBody: RequestBody) =
        callApi(Constants.LOGIN_API, requestBody)

    suspend fun callSendOtp(requestBody: RequestBody) = callApi(Constants.SEND_OTP, requestBody)

    suspend fun callVerifyOtp(requestBody: RequestBody) = callApi(Constants.VERIFY_OTP, requestBody)

    suspend fun getProfileDataList() =
        callApi(Constants.PROFILE_LIST_DATA, Utility.convertToRequestBody(mapOf())!!)

    suspend fun getProfileFirstData(requestBody: RequestBody) =
        callApi(Constants.COMPLETE_PROFILE_FIRST, requestBody)

    suspend fun getProfileSecondData(imageFile: Array<MultipartBody.Part?>) =
        callApiPhotos(Constants.COMPLETE_PROFILE_SECOND, imageFile)

    suspend fun getCompleteSurvey(requestBody: RequestBody) =
        callApi(Constants.COMPLETE_SURVERY_FIRST, requestBody)

    suspend fun getPlanDetails() =
        callApi(Constants.PLAN_DETAILS, Utility.convertToRequestBody(mapOf())!!)

    suspend fun getLiveUpdate() =
        callApi(Constants.LIVE_UPDATE, Utility.convertToRequestBody(mapOf())!!)

    suspend fun getSaveTransaction(requestBody: RequestBody) = callApi(Constants.SAVE_TRANSACTION, requestBody)

    suspend fun getSurveyData() = callApi(Constants.SURVEY_SETUP, Utility.convertToRequestBody(mapOf())!!)

    suspend fun getMatchList(requestBody: RequestBody) = callApi(Constants.MATCH_LIST, requestBody)

    suspend fun getUserData(params: Map<String, String>) =
        callGetApi(Constants.USER_DETAILS, params)

    suspend fun getSaveData(params: Map<String, String>) = callGetApi(Constants.SAVE_LIST, params)

    suspend fun getUserDislike(params: Map<String, String>) =
        callGetApi(Constants.USER_DISLIKE, params)

    suspend fun getLikeDetails(params: Map<String, String>) =
        callGetApi(Constants.LIKE_DETAILS, params)

    suspend fun getRemoveUser(requestBody: RequestBody) =
        callApi(Constants.REMOVE_USER, requestBody)

    suspend fun checkActivePlan() =
        callApi(Constants.CHECK_ACTIVE_PLAN, Utility.convertToRequestBody(mapOf())!!)

    suspend fun getMyProfile() =
        callApi(Constants.MY_PROFILE, Utility.convertToRequestBody(mapOf())!!)

    suspend fun getNotificationList() =
        callApi(Constants.NOTIFICATION_LIST, Utility.convertToRequestBody(mapOf())!!)

    suspend fun getLikeList() =
        callApi(Constants.LIKE_LIST, Utility.convertToRequestBody(mapOf())!!)

    suspend fun getApperance() = callApi(Constants.APPEARENCE, Utility.convertToRequestBody(mapOf())!!)

    suspend fun clearNotification() = callApi(Constants.CLEAR_NOTIFICATION, Utility.convertToRequestBody(mapOf())!!)

    suspend fun deactivateAccount() = callApi(Constants.DEACTIVATE_ACCOUNT, Utility.convertToRequestBody(mapOf())!!)

}

