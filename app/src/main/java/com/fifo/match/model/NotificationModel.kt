package com.fifo.match.model


import com.google.gson.annotations.SerializedName

data class NotificationModel(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("from_id")
    val fromId: Int?,
    @SerializedName("to_id")
    val toId: Int?,
    @SerializedName("open_id")
    val openId: Int?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("subtitle")
    val subtitle: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("is_read")
    val isRead: Boolean?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("deleted_at")
    val deletedAt: Any?,
    @SerializedName("photo")
    val photo: Photo?
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
}

