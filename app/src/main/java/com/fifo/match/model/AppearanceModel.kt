package com.fifo.match.model


import com.google.gson.annotations.SerializedName

data class AppearanceModel(
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
    @SerializedName("plan_expire_date")
    val planExpireDate: String?,
    @SerializedName("age")
    val age: Int?,
    @SerializedName("questionnaire")
    val questionnaire: Questionnaire?,
    @SerializedName("relationship_data")
    val relationshipData: RelationshipData?,
    @SerializedName("occupation")
    val occupation: Occupation?,
    @SerializedName("education")
    val education: Education?
) {

    data class Questionnaire(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("user_id")
        val userId: Int?,
        @SerializedName("height_type")
        val heightType: Any?,
        @SerializedName("height")
        val height: String?,
        @SerializedName("body_type")
        val bodyType: String?,
        @SerializedName("seeking")
        val seeking: Any?,
        @SerializedName("qualities_appreciate")
        val qualitiesAppreciate: Any?,
        @SerializedName("personality_types")
        val personalityTypes: String?,
        @SerializedName("good_looking")
        val goodLooking: Any?,
        @SerializedName("kids")
        val kids: Any?,
        @SerializedName("kids_in_future")
        val kidsInFuture: Any?,
        @SerializedName("my_health")
        val myHealth: String?,
        @SerializedName("other_health")
        val otherHealth: String?,
        @SerializedName("relevant")
        val relevant: Any?,
        @SerializedName("my_qualities")
        val myQualities: Any?,
        @SerializedName("other_qualities")
        val otherQualities: Any?,
        @SerializedName("personality")
        val personality: Any?,
        @SerializedName("interested_fact")
        val interestedFact: String?,
        @SerializedName("decision")
        val decision: String?,
        @SerializedName("prefer_clarity")
        val preferClarity: String?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("updated_at")
        val updatedAt: String?,
        @SerializedName("deleted_at")
        val deletedAt: Any?
    )

    data class RelationshipData(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("created_at")
        val createdAt: String?
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

    data class Education(
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