package com.fifo.match.network.api

import com.fifo.match.model.*
import com.fifo.match.network.entities.Result
import com.fifo.match.network.utils.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import java.util.LinkedHashMap

interface ApiService {

    @POST(Constants.LOGIN_OTP_API)
    suspend fun loginOTPApi(@Body requestBody: RequestBody): Response<Result<OtpModel>>

    @POST(Constants.LOGIN_API)
    suspend fun verifyLoginOTPApi(@Body requestBody: RequestBody): Response<Result<SignupModel>>

    @POST(Constants.SEND_OTP)
    suspend fun sendOtp(@Body requestBody: RequestBody): Response<Result<OtpModel>>

    @POST(Constants.VERIFY_OTP)
    suspend fun verifyOtp(@Body requestBody: RequestBody): Response<Result<SignupModel>>

    @GET(Constants.PROFILE_LIST_DATA)
    suspend fun getProfileData() : Response<Result<CompleteProfileListBean>>

    @POST(Constants.COMPLETE_PROFILE_FIRST)
    suspend fun getProfileFirstData(@Body requestBody: RequestBody?,@Header(Constants.X_ACCESS_TOKEN) token:String): Response<Result<Any>>

    @Multipart
    @POST(Constants.COMPLETE_PROFILE_SECOND)
    suspend fun getProfileSecondData(@Part imageFile:Array<MultipartBody.Part?>,@Header(Constants.X_ACCESS_TOKEN) token:String):  Response<Result<Any>>

    @POST(Constants.COMPLETE_SURVERY_FIRST)
    suspend fun getCompleteSurvey(@Body requestBody: RequestBody,@Header(Constants.X_ACCESS_TOKEN) token:String):  Response<Result<Any>>

    @GET(Constants.PLAN_DETAILS)
    suspend fun getPlanDetails(@Header(Constants.X_ACCESS_TOKEN) token:String) : Response<Result<List<PlanModel>>>

    @POST(Constants.LIVE_UPDATE)
    suspend fun getLiveUpdate(@Header(Constants.X_ACCESS_TOKEN) token:String):  Response<Result<Any>>

    @POST(Constants.SAVE_TRANSACTION)
    suspend fun getSaveTransaction(@Body requestBody: RequestBody,@Header(Constants.X_ACCESS_TOKEN) token:String):  Response<Result<Any>>

    @GET(Constants.SURVEY_SETUP)
    suspend fun getSurveyData(@Header(Constants.X_ACCESS_TOKEN) token:String) : Response<Result<SurveyModel>>

    @POST(Constants.MATCH_LIST)
    suspend fun getMatchList(@Body requestBody: RequestBody,@Header(Constants.X_ACCESS_TOKEN) token:String) : Response<Result<MatchListModel>>

    @GET(Constants.USER_DETAILS)
    suspend fun getUserData(@QueryMap params: Map<String, String>, @Header(Constants.X_ACCESS_TOKEN) token:String) : Response<Result<UserDetailsModel>>

    @GET(Constants.SAVE_LIST)
    suspend fun getSaveData(@QueryMap params: Map<String, String>, @Header(Constants.X_ACCESS_TOKEN) token:String) : Response<Result<LikeSavedModel>>

    @POST(Constants.USER_SAVE_Like)
    suspend fun getSaveLikeCancel(@Body requestBody: RequestBody?,@Header(Constants.X_ACCESS_TOKEN) token:String):  Response<Result<Any>>

    @POST(Constants.REMOVE_USER)
    suspend fun getRemoveUser(@Body requestBody: RequestBody?,@Header(Constants.X_ACCESS_TOKEN) token:String):  Response<Result<Any>>

    @GET(Constants.CHECK_ACTIVE_PLAN)
    suspend fun checkActivePlan(@Header(Constants.X_ACCESS_TOKEN) token:String) : Response<Result<CheckPlanModel>>

    @GET(Constants.MY_PROFILE)
    suspend fun getMyProfile(@Header(Constants.X_ACCESS_TOKEN) token:String) : Response<Result<ProfileModel>>

    @Multipart
    @POST(Constants.MY_PROFILE)
    suspend fun updateProfile(@PartMap requestBody: LinkedHashMap<String, RequestBody>, @Part images: List<MultipartBody.Part>, @Header(Constants.X_ACCESS_TOKEN) token:String): Response<Result<Any>>

    @POST(Constants.ONLINE_OFFLINE)
    suspend fun getOnlineOffline(@Body requestBody: RequestBody?,@Header(Constants.X_ACCESS_TOKEN) token:String):  Response<Result<Any>>

    @POST(Constants.NOTIFICATION_STATUS)
    suspend fun getNotification(@Body requestBody: RequestBody?,@Header(Constants.X_ACCESS_TOKEN) token:String):  Response<Result<Any>>

    @GET(Constants.NOTIFICATION_LIST)
    suspend fun getNotificationList(@Header(Constants.X_ACCESS_TOKEN) token:String) : Response<Result<ArrayList<NotificationModel>>>

    @POST(Constants.USER_LIKE)
    suspend fun getUserLike(@Body requestBody: RequestBody?,@Header(Constants.X_ACCESS_TOKEN) token:String):  Response<Result<Any>>

    @GET(Constants.USER_DISLIKE)
    suspend fun getUserDislike(@QueryMap params: Map<String, String>, @Header(Constants.X_ACCESS_TOKEN) token:String) : Response<Result<Any>>

    @GET(Constants.LIKE_LIST)
    suspend fun getLikeList(@Header(Constants.X_ACCESS_TOKEN) token:String) : Response<Result<ArrayList<LikeNewModel>>>

    @GET(Constants.LIKE_DETAILS)
    suspend fun getLikeDetails(@QueryMap params: Map<String, String>, @Header(Constants.X_ACCESS_TOKEN) token:String) : Response<Result<MatchModal>>

    @GET(Constants.APPEARENCE)
    suspend fun getAppearance(@Header(Constants.X_ACCESS_TOKEN) token:String) : Response<Result<AppearanceModel>>

    @POST(Constants.CREATE_CHAT)
    suspend fun createChat(@Body requestBody: RequestBody?,@Header(Constants.X_ACCESS_TOKEN) token:String):  Response<Result<CreateChatModel>>

    @GET(Constants.CLEAR_NOTIFICATION)
    suspend fun clearNotification(@Header(Constants.X_ACCESS_TOKEN) token:String) : Response<Result<ArrayList<Any>>>

    @POST(Constants.SEND_CHAT_NOTIFICATION)
    suspend fun sendChatNotification(@Body requestBody: RequestBody?,@Header(Constants.X_ACCESS_TOKEN) token:String):  Response<Result<Any>>

    @POST(Constants.USER_VERIFICATION)
    suspend fun userVerification(@Body requestBody: RequestBody?,@Header(Constants.X_ACCESS_TOKEN) token:String):  Response<Result<Any>>

    @POST(Constants.SIGN_OUT)
    suspend fun signOut(@Body requestBody: RequestBody?,@Header(Constants.X_ACCESS_TOKEN) token:String):  Response<Result<Any>>

    @POST(Constants.DEACTIVATE_ACCOUNT)
    suspend fun deactivateAccount(@Body requestBody: RequestBody?,@Header(Constants.X_ACCESS_TOKEN) token:String):  Response<Result<Any>>

    @POST(Constants.DELETE_PHOTO)
    suspend fun getDeletePhoto(@Body requestBody: RequestBody?,@Header(Constants.X_ACCESS_TOKEN) token:String):  Response<Result<Any>>

    @POST(Constants.DELETE_CHAT)
    suspend fun getDeleteCHAT(@Body requestBody: RequestBody?,@Header(Constants.X_ACCESS_TOKEN) token:String):  Response<Result<Any>>

}