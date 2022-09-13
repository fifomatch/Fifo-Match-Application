package com.fifo.match.model


import com.google.gson.annotations.SerializedName

data class LikeListModel(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("sender_id")
    val senderId: Int?,
    @SerializedName("receiver_id")
    val receiverId: Int?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("deleted_at")
    val deletedAt: Any?,
    @SerializedName("receiver")
    val receiver: Receiver?
) {

        data class Receiver(
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
            val appVersion: String?,
            @SerializedName("os_version")
            val osVersion: String?,
            @SerializedName("auth_token")
            val authToken: String?,
            @SerializedName("latitude")
            val latitude: String?,
            @SerializedName("longitude")
            val longitude: String?,
            @SerializedName("current_latitude")
            val currentLatitude: String?,
            @SerializedName("current_longitude")
            val currentLongitude: String?,
            @SerializedName("last_activity")
            val lastActivity: Any?,
            @SerializedName("is_verified")
            val isVerified: Boolean?,
            @SerializedName("relationship_status_id")
            val relationshipStatusId: Int?,
            @SerializedName("occupation_id")
            val occupationId: Int?,
            @SerializedName("education_id")
            val educationId: Int?,
            @SerializedName("is_pause")
            val isPause: Boolean?,
            @SerializedName("flowers")
            val flowers: Int?,
            @SerializedName("profile_complete")
            val profileComplete: Int?,
            @SerializedName("created_at")
            val createdAt: String?,
            @SerializedName("updated_at")
            val updatedAt: String?,
            @SerializedName("deleted_at")
            val deletedAt: Any?,
            @SerializedName("working_fifo")
            val workingFifo: String?,
            @SerializedName("swing")
            val swing: String?,
            @SerializedName("min_age")
            val minAge: Int?,
            @SerializedName("max_age")
            val maxAge: Int?,
            @SerializedName("login_time")
            val loginTime: String?,
            @SerializedName("country_code")
            val countryCode: String?,
            @SerializedName("country_name")
            val countryName: String?,
            @SerializedName("surey_status")
            val sureyStatus: Int?,
            @SerializedName("user_plan")
            val userPlan: Any?,
            @SerializedName("login_status")
            val loginStatus: String?,
            @SerializedName("notification_status")
            val notificationStatus: String?,
            @SerializedName("age")
            val age: Int?,
            @SerializedName("photo")
            val photo: Photo?,
            @SerializedName("occupation")
            val occupation: Occupation?
        ) {
            data class Photo(
                @SerializedName("id")
                val id: Int?,
                @SerializedName("user_id")
                val userId: Int?,
                @SerializedName("type")
                val type: Int?,
                @SerializedName("name")
                val name: String?,
                @SerializedName("created_at")
                val createdAt: String?,
                @SerializedName("updated_at")
                val updatedAt: String?,
                @SerializedName("deleted_at")
                val deletedAt: Any?
            )

            data class Occupation(
                @SerializedName("id")
                val id: Int?,
                @SerializedName("name")
                val name: String?,
                @SerializedName("status")
                val status: String?,
                @SerializedName("created_at")
                val createdAt: String?
            )
        }

}