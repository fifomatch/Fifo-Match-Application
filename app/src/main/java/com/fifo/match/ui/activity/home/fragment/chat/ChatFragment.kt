package com.fifo.match.ui.activity.home.fragment.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fifo.match.App
import com.fifo.match.ui.activity.chat.Constant
import com.fifo.match.databinding.FragmentChatBinding
import com.fifo.match.network.utils.Utility
import com.fifo.match.ui.base.BaseFragment
import com.google.firebase.database.*
import com.fifo.match.R
import com.fifo.match.model.UserDetailsModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.ui.activity.chat.ArchivedChatsActivity
import com.fifo.match.ui.activity.completeProfile.fragment.ProfileFirstFragment
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_chat.*
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ChatFragment : BaseFragment<ChatViewModel,FragmentChatBinding>(ChatViewModel::class.java) {

    private val viewModel: ChatViewModel by activityViewModels()

    lateinit var mAdapter: ChatListAdapter
    lateinit var mContextCompat: Activity
    lateinit var mUserDataBase: DatabaseReference
    lateinit var mGroupDataBase: DatabaseReference

    var mGroupMetaDataList: ArrayList<GroupMetaData>? = null
    var mArrayIdsList = ArrayList<String>()

    private var isUpdated = false

    override fun getLayoutRes()=R.layout.fragment_chat
    @SuppressLint("ClickableViewAccessibility")
    override fun onRender() {
        super.onRender()
        binding.lifecycleOwner = this
        mContextCompat = requireActivity()

        try {
            mkk = 0
            setUpFirebaseDB()
            updateAdapterData()
            isUpdated = true
        } catch (e: java.lang.Exception) {
        }

        binding.archived.setOnClickListener{
            requireActivity().openActivity<ArchivedChatsActivity>("listData" to mGroupMetaDataList!!)
        }

        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(value: Editable) {
                searchChats(value.toString())
            }
        })

        getObserver()
    }

    fun searchChats(searchKey: String) {
        try {
            if (searchKey.isNotEmpty()) {
                val temp: ArrayList<GroupMetaData> = ArrayList()
                if (searchKey.isEmpty()) {
                    if (mAdapter != null) {
                        mAdapter.updateDataModel(mGroupMetaDataList!!) {
                            mGroupMetaDataList = it
                        }
                        mAdapter.notifyDataSetChanged()
                    }
                } else {
                    for (d in mGroupMetaDataList!!) {
                        val name: String = d.group_name.toLowerCase().toString() + " " + d.mLastMessage.toLowerCase()
                        if (name.contains(searchKey.toLowerCase())) {
                            temp.add(d)
                        }
                    }
                    if (mAdapter != null) {
                        mAdapter.updateDataModel(temp) {
                        }
                        mAdapter.notifyDataSetChanged()
                    }else{
                        binding.tvNoFound.isVisible = true
                        binding.recyclerGroupChat.isVisible = false
                    }
                }
            } else {
                if (mAdapter != null) {
                    mAdapter.updateDataModel(mGroupMetaDataList!!) {
                        mGroupMetaDataList = it
                    }
                    mAdapter.notifyDataSetChanged()
                } else{
                    binding.tvNoFound.isVisible = true
                    binding.recyclerGroupChat.isVisible = false
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    // Firebase db initalization
    private fun setUpFirebaseDB() {
        mGroupMetaDataList = ArrayList()
        mArrayIdsList = ArrayList()
        mUserDataBase = App.mUserDatabase!!
        mGroupDataBase = FirebaseDatabase.getInstance().reference.child(Constant.FIREBASE_GROUP_TABLE)
        mUserDataBase.addValueEventListener(mUserEventListener!!)
    }

    private fun updateAdapterData() {
        mAdapter = ChatListAdapter(mGroupMetaDataList, requireActivity(),object : ChatListAdapter.GetCLick {
            override fun getChatDelete(position: Int,node:String) {
                viewModel.callDeleteChatApi(node)
            }
        })
        binding.recyclerGroupChat.layoutManager = LinearLayoutManager(requireActivity())
        binding.homeAdapter = mAdapter

        /*if (mAdapter == null){
            binding.txtNoData.isVisible = true
        } else {
            binding.txtNoData.isVisible = false
        }*/

    }

    // Get Chat node

    var mkk: Int = 0
    var totalCount: Int = 0
    private var mUserEventListener: ValueEventListener? = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            try {
                if (dataSnapshot.child("chat_room").exists()) {
                    mkk = 0
                    val mChilds = dataSnapshot.child("chat_room")
                    totalCount = mChilds.children.count()
                    for (childNode in mChilds.children) {
                        mkk += 1
                        try {
                            mGroupMetaDataList!!.add(GroupMetaData("", childNode.value.toString(), "",
                                "", "", "", "", "", 0,
                                "", 0L, "", "", ""
                            ))
                            mArrayIdsList.add(childNode.value.toString())

                            mGroupDataBase.child(childNode.value.toString())
                                .addValueEventListener(mGroupInfoEventListener!!)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: java.lang.Exception) {
            }
        }
        override fun onCancelled(databaseError: DatabaseError) {}
    }

    var mChangedID = ""
    var isGroup = ""
    var is_archived = ""
    var is_online = ""
    var group_id = ""
    var group_image = ""
    var group_name = ""
    var createdby = ""
    var firebase_id = ""
    var lastMessage = ""
    var messages_type = ""
    var time = ""
    var timeStamp = 0L
    var messageCount: Int = 0

    private var mGroupInfoEventListener: ValueEventListener? = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            try {
                if (dataSnapshot.value == null) {
                    if (mArrayIdsList.contains(dataSnapshot.key)) {
                        val mPos = mArrayIdsList.indexOf(mChangedID)
                        if (mPos >= 0)
                            mGroupMetaDataList!!.removeAt(mPos)
                        mAdapter.updateDataModel(mGroupMetaDataList!!) {
                            mGroupMetaDataList = it
                            tvNoFound.isVisible = it.isEmpty()
                        }
                    }
                }
                if (userData?.id != null) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.hasChild("group_id")) {
                            mChangedID = dataSnapshot.child("group_id").value.toString()
                        }
                        if (dataSnapshot.hasChild("is_group")) {
                            isGroup = dataSnapshot.child("is_group").value.toString()
                        }
                        if (isGroup == "1" || isGroup == "") {
                            if (dataSnapshot.child(Constant.FIREBASE_GROUP_INFO).exists()) {
                                val mChilds = dataSnapshot.child(Constant.FIREBASE_GROUP_INFO)
                                val map = mChilds.value as Map<*, *>
                                group_id = map["group_id"].toString()
                                group_image = map["group_image"].toString()
                                group_name = map["group_name"].toString()
                                createdby = map["createdby"].toString()
                            }
                        } else {
                            val mID = mChangedID.split("_")
                            var mOtherUserID = ""
                            mID.indices.forEach { i ->
                                if (userData!!.id.toString() != mID[i]) {
                                    mOtherUserID = mID[i]
                                }
                            }
                            createdby = ""
                            if (dataSnapshot.child(Constant.FIREBASE_GROUP_INFO).exists()) {
                                val mChilds = dataSnapshot.child(Constant.FIREBASE_GROUP_INFO)
                                val map = mChilds.value as Map<*, *>
                                createdby = map["createdby"].toString()
                            }
                            group_image = ""
                            group_name = ""
                            is_archived = ""
                            is_online = ""
                            firebase_id = ""
                            val mChilds = dataSnapshot.child(Constant.FIREBASE_GROUP_MEMBERS).child("user_" + mOtherUserID)
                            val mHashMap = mChilds.value as HashMap<*, *>

                            group_image = mHashMap["user_image"].toString()
                            group_name = mHashMap["user_name"].toString()
                            firebase_id = mHashMap["firebase_id"].toString()

                            is_online = if (mHashMap["is_online"].toString().isEmpty()) {
                                "0"
                            } else {
                                mHashMap["is_online"].toString()
                            }
                             is_archived = if (mHashMap["is_archived"].toString().isEmpty()) {
                                "0"
                            } else {
                                mHashMap["is_archived"].toString()
                            }

                            if (mGroupMetaDataList!!.isNotEmpty()) {
                                viewModel.chatListFound.value = true
                            }
                            lastMessage = ""
                            messages_type = ""
                            time = ""
                            timeStamp = 0L
                            if (dataSnapshot.child(Constant.FIREBASE_LAST_MESSAGES_DETAILS).exists()) {
                                val mChild = dataSnapshot.child(Constant.FIREBASE_LAST_MESSAGES_DETAILS)
                                val map = mChild.value as Map<*, *>
                                lastMessage = map["message"].toString()
                                messages_type = map["messages_type"].toString()
                                time = ""
                                timeStamp = 0L
                                if (map["time"].toString().isNotEmpty()) {
                                    timeStamp = map["time"].toString().toLong()
                                    time = getDateTime(map["time"].toString().toLong())!!
                                }

                            }
                            if (dataSnapshot.child(Constant.FIREBASE_UNREAD_MESSAGES_COUNT)
                                    .exists()
                            ) {
                                messageCount = 0
                                val mChild = dataSnapshot.child(Constant.FIREBASE_UNREAD_MESSAGES_COUNT)
                                if (mChild.child("user_" + userData!!.id.toString()).exists()) {
                                    messageCount = mChild.child("user_" + userData!!.id.toString()).value.toString().toInt()
                                }
                                var mImageType: Int = 0
                                when (messages_type) {
                                    "image" -> {
                                        mImageType = R.drawable.photo
                                    }
                                }
                                var mMessageCout = ""
                                if (messageCount > 50) {
                                    mMessageCout = "50+"
                                } else {
                                    mMessageCout = messageCount.toString()
                                }
                                val mGroupMetaData = GroupMetaData(createdby, mChangedID, group_name, group_image, lastMessage,
                                    mMessageCout, time, messages_type, mImageType, isGroup, timeStamp, is_online, is_archived, firebase_id)
                                if (checkIdExistOrNot(mChangedID, group_name, group_image)) {
                                    if (lastMessage.isEmpty()) { ""
                                    } else {
                                        checkData(mChangedID, mGroupMetaData)
                                    }
                                } else {
                                    checkData(mChangedID, mGroupMetaData)
                                }
                                val sortedList = mGroupMetaDataList!!.sortedWith(compareBy { it.timestamp }).reversed()
                                mGroupMetaDataList!!.clear()
                                mGroupMetaDataList!!.addAll(sortedList)
                                try {
                                    val mDataList = mGroupMetaDataList!!.filter { it.is_archived == "0" || it.is_archived == "" } as ArrayList<GroupMetaData>
                                    if (mDataList.size == 0) {
                                        binding.recyclerGroupChat.visibility = View.GONE
                                        binding.tvNoFound.visibility = View.VISIBLE
                                    } else {
                                        binding.recyclerGroupChat.visibility = View.VISIBLE
                                        binding.tvNoFound.visibility = View.GONE
                                    }
                                    mAdapter.updateDataModel(mGroupMetaDataList!!) {

                                    }
                                } catch (e: Exception) {
                                }
                            }
                        }
                    }
                }

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    fun getDateTime(timestamp: Long): String? {
        return try {
            val sdf1 = SimpleDateFormat("dd MMM yyyy")
            var str1: String? = ""
            val netDate = Date(timestamp)
            val c1 = Calendar.getInstance() // today
            c1.add(Calendar.DAY_OF_YEAR, -1) // yesterday
            val c2 = Calendar.getInstance()
            c2.time = netDate // your date
            str1 = if (sdf1.format(netDate) == sdf1.format(Date().time)) {
                timeValue(timestamp.toString())

            } else if (c1[Calendar.YEAR] == c2[Calendar.YEAR]
                && c1[Calendar.DAY_OF_YEAR] == c2[Calendar.DAY_OF_YEAR]
            ) {
                getString(R.string.yesterday)
            } else {
                sdf1.format(netDate)
            }
            str1
        } catch (ex: Exception) {
            "xx"
        }
    }

    private fun timeValue(time: String): String {
        try {
            var timeNew: String = Utility.getFormattedDateNotification(requireContext(), time)!!
            if (timeNew.equals("0m", ignoreCase = true)) {
                timeNew = "Now"
            } else if (timeNew.equals("0 min", ignoreCase = true)) {
                timeNew = "Now"
            } else if (timeNew.equals("+0 min", ignoreCase = true)) {
                timeNew = "Now"
            } else if (timeNew.equals("In 0 minutes", ignoreCase = true)) {
                timeNew = "Now"
            }
            return timeNew
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    var position = -1
    fun checkIdExistOrNot(group_id: String, group_name: String, group_image: String): Boolean {
        var isExist = false
        for (i in mGroupMetaDataList!!.indices) {
            if (mGroupMetaDataList!![i].group_id == group_id) {
                mGroupMetaDataList!![i].group_image = group_image
                mGroupMetaDataList!![i].group_name = group_name
                isExist = true
                position = i
                return isExist
            }
        }
        return isExist
    }

    fun checkData(id: String, data: GroupMetaData) {
        for (i in mGroupMetaDataList!!.indices) {
            if (mGroupMetaDataList!![i].group_id == id) {
                if (data.group_name.isNotEmpty())
                    mGroupMetaDataList!![i].group_name = data.group_name

                mGroupMetaDataList!![i].isGroup = data.isGroup
                mGroupMetaDataList!![i].createdby = data.createdby

                if (data.group_image.isNotEmpty())
                    mGroupMetaDataList!![i].group_image = data.group_image

                if (data.mLastMessage.isNotEmpty())
                    mGroupMetaDataList!![i].mLastMessage = data.mLastMessage

                mGroupMetaDataList!![i].sampleImage = data.sampleImage
                mGroupMetaDataList!![i].messages_type = data.messages_type
                mGroupMetaDataList!![i].time = data.time

                if (data.is_archived.isNotEmpty())
                    mGroupMetaDataList!![i].is_archived = data.is_archived

                if (data.is_online.isNotEmpty())
                    mGroupMetaDataList!![i].is_online = data.is_online

                if (data.timestamp != 0L)
                    mGroupMetaDataList!![i].timestamp = data.timestamp
                mGroupMetaDataList!![i].mMessageCount = data.mMessageCount

                if (data.firebase_id != null) {
                    mGroupMetaDataList!![i].firebase_id = data.firebase_id
                }

                break
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            if (mUserEventListener != null) {
                mUserDataBase.removeEventListener(mUserEventListener!!)
                mUserDataBase.onDisconnect()
                mUserDataBase.ref.onDisconnect()
            }
            if (mGroupInfoEventListener != null) {
                mGroupDataBase.removeEventListener(mGroupInfoEventListener!!)
                mGroupDataBase.onDisconnect()
                mGroupDataBase.ref.onDisconnect()
            }
            mUserEventListener = null
            mGroupInfoEventListener = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getObserver() {
        viewModel.responseDeleteChat.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(requireActivity())
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                    }
                }
                is NetworkResult.Error -> {
                    ShowProgressDialog.hideProgress()
                    requireActivity().toastError(response.exception.localizedMessage!!.toString())
                }
            }
        }
    }
}