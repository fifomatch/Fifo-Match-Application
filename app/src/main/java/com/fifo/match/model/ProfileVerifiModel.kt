package com.fifo.match.model


import com.google.gson.annotations.SerializedName

data class ProfileVerifiModel(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val `data`: Data?
) {
    data class Data(
        @SerializedName("profile_complete")
        val profileComplete: Int?
    )
}