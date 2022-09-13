package com.fifo.match.model


import com.google.gson.annotations.SerializedName

data class CheckPlanModel(
    @SerializedName("plan_id")
    val planId: String?,
    @SerializedName("start_datetime")
    val startDate: String?,
    @SerializedName("end_datetime")
    val endDate: String?,

)