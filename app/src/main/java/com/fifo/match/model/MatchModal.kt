package com.fifo.match.model


import com.google.gson.annotations.SerializedName

data class MatchModal(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("sender_id")
    val senderId: Int?,
    @SerializedName("receiver_id")
    val receiverId: Int?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("deleted_at")
    val deletedAt: Any?,
    @SerializedName("sender_photo")
    val senderPhoto: SenderPhoto?,
    @SerializedName("receiver_photo")
    val receiverPhoto: ReceiverPhoto?
) {
    data class SenderPhoto(
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

    data class ReceiverPhoto(
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