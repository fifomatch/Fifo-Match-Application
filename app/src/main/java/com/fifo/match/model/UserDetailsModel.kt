package com.fifo.match.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserDetailsModel(
    @SerializedName("user_details")
    val userDetails: UserDetails?
):Serializable {
    data class UserDetails(
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
        @SerializedName("verify")
        val isVerified: Int?,
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
        val minAge: Any?,
        @SerializedName("max_age")
        val maxAge: Any?,
        @SerializedName("login_time")
        val loginTime: String?,
        @SerializedName("country_code")
        val countryCode: Any?,
        @SerializedName("country_name")
        val countryName: String?,
        @SerializedName("surey_status")
        val sureyStatus: Int?,
        @SerializedName("miles")
        val miles: String?,
        @SerializedName("age")
        val age: Int?,
        @SerializedName("is_blocked")
        val isBlocked: Boolean?,
        @SerializedName("is_like")
        val isLike: Boolean?,
        @SerializedName("is_chat")
        val isChat: Boolean?,
        @SerializedName("is_saved")
        val isSaved: Boolean?,
        @SerializedName("photos")
        val photos: List<Photo>,
        @SerializedName("questionnaire")
        val questionnaire: Questionnaire?,
        @SerializedName("occupation")
        val occupation: Occupation?,
        @SerializedName("relationship_data")
        val relationshipData: RelationshipData?,
        @SerializedName("education")
        val education: Education?,
        @SerializedName("user_inapp_transaction")
        val userInappTransaction: UserInappTransaction?
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

        data class Questionnaire(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("user_id")
            val userId: Int?,
            @SerializedName("height_type")
            val heightType: String?,
            @SerializedName("height")
            val height: String?,
            @SerializedName("body_type")
            val bodyType: String?,
            @SerializedName("seeking")
            val seeking: String?,
            @SerializedName("qualities_appreciate")
            val qualitiesAppreciate: String?,
            @SerializedName("personality_types")
            val personalityTypes: String?,
            @SerializedName("good_looking")
            val goodLooking: String?,
            @SerializedName("kids")
            val kids: String?,
            @SerializedName("kids_in_future")
            val kidsInFuture: String?,
            @SerializedName("my_health")
            val myHealth: String?,
            @SerializedName("other_health")
            val otherHealth: String?,
            @SerializedName("relevant")
            val relevant: Any?,
            @SerializedName("my_qualities")
            val myQualities: String?,
            @SerializedName("other_qualities")
            val otherQualities: Any?,
            @SerializedName("personality")
            val personality: String?,
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
        data class UserInappTransaction(
            @SerializedName("transaction_id")
            val transactionId: String?,
            @SerializedName("user_id")
            val userId: Int?,
            @SerializedName("type")
            val type: String?,
            @SerializedName("plan_id")
            val planId: String?,
            @SerializedName("membership_type")
            val membershipType: String?,
            @SerializedName("membership_type_value")
            val membershipTypeValue: Int?,
            @SerializedName("amount")
            val amount: String?,
            @SerializedName("start_datetime")
            val startDatetime: String?,
            @SerializedName("end_datetime")
            val endDatetime: String?,
            @SerializedName("status")
            val status: Boolean?,
            @SerializedName("created_at")
            val createdAt: String?,
            @SerializedName("updated_at")
            val updatedAt: String?
        )
    }
}