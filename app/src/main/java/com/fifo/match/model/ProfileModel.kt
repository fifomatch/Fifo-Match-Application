package com.fifo.match.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.File
import java.io.Serializable


data class ProfileModel(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("interested_in")
    val interestedIn: String?,
    @SerializedName("dob")
    val dob: String?,
    @SerializedName("relationship_status_id")
    val relationshipStatusId: Int?,
    @SerializedName("occupation_id")
    val occupationId: Int?,
    @SerializedName("education_id")
    val educationId: Int?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("age")
    val age: Int?,
    @SerializedName("verify")
    val verify: Int?,
    @SerializedName("photos")
    val photos: List<Photo?>?,
    @SerializedName("relationship_data")
    val relationshipData: RelationshipData?,
    @SerializedName("occupation")
    val occupation: Occupation?,
    @SerializedName("education")
    val education: Education?
) : Serializable {

    data class Photo(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("user_id")
        val userId: Int?,
        @SerializedName("type")
        val type: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("nameFile")
        val nameFile: File,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("updated_at")
        val updatedAt: String?,
        @SerializedName("deleted_at")
        val deletedAt: Any?,
        ) : Serializable

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