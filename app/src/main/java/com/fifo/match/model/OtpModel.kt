package com.fifo.match.model


import com.google.gson.annotations.SerializedName

data class OtpModel(
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("otp")
    var otp: Int?
)