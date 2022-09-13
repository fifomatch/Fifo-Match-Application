package com.fifo.match.model

import com.google.gson.annotations.SerializedName

data class SignupModel(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("firebase_id")
    val firebaseId: String?,
    @SerializedName("social_type")
    val socialType: String?,
    @SerializedName("social_id")
    val socialId: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("is_email_verified")
    val isEmailVerified: Boolean?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("interested_in")
    val interestedIn: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("profile_pic")
    val profilePic: Any?,
    @SerializedName("dob")
    val dob: String?,
    @SerializedName("about")
    val about: String?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("subscription_plan")
    val subscriptionPlan: Boolean?,
    @SerializedName("device_type")
    val deviceType: String?,
    @SerializedName("app_version")
    val appVersion: Any?,
    @SerializedName("os_version")
    val osVersion: Any?,
    @SerializedName("auth_token")
    val authToken: String?,
    @SerializedName("latitude")
    val latitude: Any?,
    @SerializedName("longitude")
    val longitude: Any?,
    @SerializedName("current_latitude")
    val currentLatitude: Any?,
    @SerializedName("current_longitude")
    val currentLongitude: Any?,
    @SerializedName("last_activity")
    val lastActivity: Any?,
    @SerializedName("is_verified")
    val isVerified: Boolean?,
    @SerializedName("relationship_status_id")
    val relationshipStatusId: Any?,
    @SerializedName("occupation_id")
    val occupationId: Any?,
    @SerializedName("education_id")
    val educationId: Any?,
    @SerializedName("is_pause")
    val isPause: Boolean?,
    @SerializedName("flowers")
    val flowers: Int?,
    @SerializedName("profile_complete")
    var profileComplete: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("deleted_at")
    val deletedAt: Any?,
    @SerializedName("working_fifo")
    val workingFifo: Any?,
    @SerializedName("swing")
    val swing: Any?,
    @SerializedName("min_age")
    val minAge: Any?,
    @SerializedName("max_age")
    val maxAge: Any?,
    @SerializedName("login_time")
    val loginTime: Any?,
    @SerializedName("country_code")
    val countryCode: String?,
    @SerializedName("country_name")
    val countryName: String?,
    @SerializedName("login_status")
    var loginStatus: String?,
    @SerializedName("notification_status")
    var notificationStatus: String?,
    @SerializedName("age")
    val age: Int?,//
    @SerializedName("surey_status")
    var sureyStatus: Int?,
    @SerializedName("is_subscribed")
    var isSubscribed: Boolean?,
)