package com.fifo.match.model


import com.google.gson.annotations.SerializedName

data class MatchListModel(
    @SerializedName("premium_user")
    val premiumUser: ArrayList<User>,
    @SerializedName("online_users")
    val onlineUsers: ArrayList<User>,
    @SerializedName("new_users")
    val newUsers: ArrayList<User>
) {

    data class User(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("dob")
        val dob: String?,
        @SerializedName("country_name")
        val countryName: Any?,
        @SerializedName("age")
        val age: Int?,
        @SerializedName("photo")
        val photo: Photo?,
        @SerializedName("occupation")
        val occupation: Occupation?,
        @SerializedName("user_verified")
        val userVerified: UserVerified?
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
        data class UserVerified(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("user_id")
            val userId: Int?,
            @SerializedName("photo")
            val photo: String?,
            @SerializedName("is_accepted")
            val isAccepted: Boolean?,
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