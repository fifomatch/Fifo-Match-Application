package com.fifo.match.ui.activity.home.fragment.chat

import java.io.Serializable

class GroupMetaData(
        var createdby: String,
        var group_id: String,
        var group_name: String,
        var group_image: String,
        var mLastMessage: String,
        var mMessageCount: String,
        var time: String,
        var messages_type: String,
        var sampleImage: Int,
        var isGroup: String,
        var timestamp: Long,
        var is_online: String="0",
        var is_archived: String="0",
        var firebase_id:String
    ): Serializable