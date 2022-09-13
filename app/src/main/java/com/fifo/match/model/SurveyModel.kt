package com.fifo.match.model


import com.google.gson.annotations.SerializedName

data class SurveyModel(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("user_id")
    val userId: Int?,
    @SerializedName("height_type")
    val heightType: String?,
    @SerializedName("height")
    val height: Int?,
    @SerializedName("body_type")
    val bodyType: String?,
    @SerializedName("seeking")
    val seeking: String?,
    @SerializedName("qualities_appreciate")
    val qualitiesAppreciate: String?,
    @SerializedName("personality_types")
    val personalityTypes: String?,
    @SerializedName("good_looking")
    val goodLooking: Any?,
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