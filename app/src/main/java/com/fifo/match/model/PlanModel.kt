package com.fifo.match.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PlanModel(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("slug")
    val slug: String?,
    @SerializedName("cms_body")
    val cmsBody: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
):Serializable