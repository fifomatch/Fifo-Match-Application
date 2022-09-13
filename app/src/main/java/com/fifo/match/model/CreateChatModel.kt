package com.fifo.match.model


import com.google.gson.annotations.SerializedName

data class CreateChatModel(
    @SerializedName("node")
    val node: String?
)