package com.fifo.match.network.entities

import com.google.gson.annotations.SerializedName

data class Result<T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message", alternate = ["msg"])
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("data")
    val result: T
)

