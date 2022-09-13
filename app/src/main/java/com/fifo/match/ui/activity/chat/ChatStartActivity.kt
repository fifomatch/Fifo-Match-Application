package com.fifo.match.ui.activity.chat

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fifo.match.App
import com.fifo.match.R
import com.fifo.match.databinding.ActivityChatStartBinding
import com.fifo.match.model.UserDetailsModel
import com.fifo.match.network.NetworkResult
import com.fifo.match.network.utils.Constants
import com.fifo.match.network.utils.Utility
import com.fifo.match.ui.activity.home.fragment.chat.ChatViewModel
import com.fifo.match.ui.activity.view.ViewActivity
import com.fifo.match.utils.GpsTracker
import com.fifo.match.utils.NetworkUtil
import com.fifo.match.utils.dialogbox.ShowProgressDialog
import com.fifo.match.utils.extensions.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

@AndroidEntryPoint
@Suppress("UNREACHABLE_CODE")
class ChatStartActivity : BaseBindingActivity(), View.OnClickListener {

    private val viewModel by viewModels<ChatViewModel>()
    private var mGroupDataBaseReferenceListener: ValueEventListener? = null
    lateinit var context: Context
    private var gpsTracker: GpsTracker? = null
    lateinit var mBinding: ActivityChatStartBinding
    lateinit var mGroupDataBase: DatabaseReference
    lateinit var mMessagesDataBase: DatabaseReference
    var mGroupID = ""
    var mChatsList: ArrayList<Chat> = ArrayList()
    var mChatAdapter: ChatAdapter? = null
    var mGroupName: String = ""
    var firebaseID: String = ""
    var mGroupImage: String = ""
    var mGroupAdminID: String = ""
    var mIsGroup: String = ""
    var selfMessageCount: Int = 0
    var otherMessageCount: Int = 0
    lateinit var lastTime: String
    var user_id = ""
    private val MY_PERMISSIONS_RECORD_AUDIO = 1


    override fun onStart() {
        super.onStart()
        App.isChatOpen = true
    }

    override fun onStop() {
        super.onStop()
        App.isChatOpen = false
    }

    override fun createContentView() {
        transparentStatusBar()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat_start)
        mBinding.lifecycleOwner = this
        selfMessageCount = 0
        otherMessageCount = 0
        registerObservers()
        getLocation()
        getObserver()
    }

    override fun createActivityObject() {
        mActivity = this
        mGroupDataBase =
            FirebaseDatabase.getInstance().reference.child(Constant.FIREBASE_GROUP_TABLE)
        mMessagesDataBase =
            FirebaseDatabase.getInstance().reference.child(Constant.FIREBASE_MESSAGES_TABLE)

    }

    /*
    Initialize Objects
     */

    override fun initializeObject() {
        context = this
        mGroupID = intent.getStringExtra("groupID")!!
        try {
            mIsGroup = intent.getStringExtra("isGroup")!!
        } catch (e: Exception) {
            mIsGroup = ""
        }
        try {
            lastTime = intent.getStringExtra("time")!!
            /*if (Math.abs(lastTime.toLong()-System.currentTimeMillis())>60000){
                mBinding.tvGroupDate.text = "Last seen "+getDateTime(lastTime.toLong())
            } else {
                mBinding.tvGroupDate.text = "Online"
            }*/
        } catch (e: Exception) {
            lastTime = ""
        }

        if (intent.hasExtra("firebaseID") != null) {
            firebaseID = intent.getStringExtra("firebaseID").toString()
            Log.d("TAGfirebaseID", firebaseID)
            initFirebase(firebaseID)
        }

        Handler().postDelayed({
            if (mChatsList.isEmpty()) {
                mBinding.mDateHeaderView.visibility = View.VISIBLE
                mBinding.mDateContainerView.text = getDateHolder(System.currentTimeMillis())
            }
        }, 1500)


        // checkAppPermissionAUDIO()
        fetchGroupMetaData()


        val str = mGroupID
        val parts = str.split("_")
        val partsOne = parts[0]
        val partsTwo = parts[1]
        if (!partsOne.equals(App.userBean?.id.toString())) {
            user_id = partsOne
        }
        if (!partsTwo.equals(App.userBean?.id.toString())) {
            user_id = partsTwo
        }
        if (user_id.isNotEmpty()) {
            viewModel.stUserID.set(user_id)
        }
        viewModel.callUserData()

    }

    private fun getLocation() {
        gpsTracker = GpsTracker(this)
        if (gpsTracker!!.canGetLocation()) {
            val latitude: Double = gpsTracker!!.latitude
            val longitude: Double = gpsTracker!!.longitude
            viewModel.stLatitude.set(latitude.toString())
            viewModel.stLongitude.set(longitude.toString())
        } else {
            gpsTracker!!.showSettingsAlert()
        }
    }


    private fun getDateHolder(timestamp: Long): String? {
        return try {
            // SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy,hh:mm aa");
            val sdf1 = SimpleDateFormat("dd MMM yyyy")
            var str1: String? = ""
            val netDate = Date(timestamp)
            val c1 = Calendar.getInstance() // today
            c1.add(Calendar.DAY_OF_YEAR, -1) // yesterday
            val c2 = Calendar.getInstance()
            c2.time = netDate // your date
            str1 = if (sdf1.format(netDate) == sdf1.format(Date().time)) {
                "Today"
            } else if (c1[Calendar.YEAR] == c2[Calendar.YEAR]
                && c1[Calendar.DAY_OF_YEAR] == c2[Calendar.DAY_OF_YEAR]
            ) {
                "Yesterday"
            } else {
                sdf1.format(netDate)
            }
            str1
        } catch (ex: Exception) {
            "xx"
        }
    }

    /*
    Fetch Chat MetaData
     */
    private fun fetchGroupMetaData() {
        if (mIsGroup == "1") {
            mBinding.txtBlock.visibility = View.GONE
            //    mBinding.imgMore.visibility = View.VISIBLE
            fetchGroupInfo()
        } else {
            mBinding.txtBlock.visibility = View.GONE
            // mBinding.imgMore.visibility = View.GONE
        }
        fetchMemberMetadata()
        fetchMessages()
    }

    var boolme = false
    var boolFriend = false
    var user_name: String = ""
    var user_image: String = ""
    private fun setPersonalChatInfo() {
        var boolme = false
        var boolFriend = false
        try {
            for (mMap in mMemberMap.values) {
                val mHashMap = mMap as HashMap<*, *>
                mHashMap["user_image"].toString()
                mHashMap["user_name"].toString()
                mHashMap["user_id"].toString()
                mHashMap["is_block"].toString()
                mHashMap["is_admin"].toString()
                if (mHashMap["user_id"].toString() != App.userBean?.id.toString()
                ) {
                    user_name = mHashMap["user_name"].toString()
                    user_image = mHashMap["user_image"].toString()

                    Glide
                        .with(mActivity!!)
                        .load(mHashMap["user_image"].toString())
                        .centerCrop()
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(mBinding.imgGroup)
                    mBinding.tvGroupTitle.text = mHashMap["user_name"].toString()


                    if (mHashMap["is_block"].toString().equals("0")) {
                        boolme = false;
                        mBinding.txtBlock.text = "Block"
                    } else {
                        boolme = true;
                        mBinding.txtBlock.text = "UnBlock"
                    }
                } else {
                    if (mHashMap["is_block"].toString().equals("0")) {
                        boolFriend = false;
                        mBinding.txtBlock.text = "Block"
                    } else {
                        boolFriend = true;
                        mBinding.txtBlock.text = "UnBlock"
                    }
                }
            }
        } catch (e: Exception) {
        }

        if (boolFriend || boolme) {
//            mBinding.layoutBottom.visibility = View.GONE
        } else {
//            mBinding.layoutBottom.visibility = View.VISIBLE
        }
    }


    override fun onResume() {
        super.onResume()
        if (mIsGroup == "1") {
//              fetchGroupInfo()
        }
        fetchMemberMetadata()
        viewModel.callUserData()
//        getChatPlanInfo()
    }

    @SuppressLint("DefaultLocale")
    private fun getHumanTimeText(milliseconds: Long): String? {
        return java.lang.String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(milliseconds),
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
        )
    }

    /*
    Fetch messages from firebase
     */
    private fun fetchMessages() {
        //  ProgressDialogView.showProgressDialog(mActivity!!)
        mGroupDataBase.child(mGroupID + "/" + Constant.FIREBASE_UNREAD_MESSAGES_COUNT)
            .child("user_" + App.userBean?.id.toString())
            .setValue(0)

        mMessagesDataBase.child(mGroupID).addValueEventListener(messageReferenceListender!!)
    }


    /*
    message reference Listener
     */
    private var mMessageCount: Long = 0
    private var messageReferenceListender: ValueEventListener? = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var count: Long = 0
            mChatsList.clear()
            mBinding.mDateHeaderView.visibility = View.GONE
            for (ds in dataSnapshot.children) {
                count += 1
                mMessageCount = count
                val map = ds.value as Map<*, *>
                val message = map["message"].toString()
                var time_stamp = ""
                val read_unread = ""
                var sender_id = ""
                var sender_image = ""
                var sender_name = ""
                var messages_type = ""
                if (map.containsKey("time")) {
                    time_stamp = map["time"].toString()
                    /*if (map["time"].toString().isNotEmpty()) {
                        formatted_time =
                            AppUtils.getDateFromUTCTimestamp(map["time"].toString().toLong())
                    }*/
                }
                /*if (map.containsKey("isRead")) {
                    read_unread = map["isRead"].toString()
                }*/
                if (map.containsKey("sender_id")) {
                    sender_id = map["sender_id"].toString()
                }
                if (map.containsKey("sender_image")) {
                    sender_image = map["sender_image"].toString()
                }
                if (map.containsKey("sender_name")) {
                    sender_name = map["sender_name"].toString()
                }
                if (map.containsKey("messages_type")) {
                    messages_type = map["messages_type"].toString()
                }

                mChatsList.add(
                    Chat(
                        sender_name,
                        read_unread, message, time_stamp, sender_id, sender_image, messages_type
                    )
                )

                for (i in 0..mChatsList.size.minus(1)) {
                    if (mChatsList[i].receiver_id == App.userBean?.id.toString()) {
                        selfMessageCount = selfMessageCount + 1
                    } else {
                        otherMessageCount = otherMessageCount + 1
                    }
                }
                if (selfMessageCount > 0 && otherMessageCount > 0) {
//                    mBinding.imgAttechement.visibility = View.VISIBLE
                } else {
//                    mBinding.imgAttechement.visibility = View.GONE
                }
                if (count == dataSnapshot.childrenCount) {
                    if (mChatAdapter == null) {
                        mBinding.recyclerChatList.layoutManager = LinearLayoutManager(mActivity)
                        mChatAdapter = ChatAdapter(mActivity!!, mChatsList, user_name, user_image)
                        mBinding.recyclerChatList.adapter = mChatAdapter
                        mBinding.recyclerChatList.scrollToPosition(mChatsList.size - 1)
                    } else {
                        mChatAdapter!!.updateData(mChatsList)
                        mBinding.recyclerChatList.scrollToPosition(mChatsList.size - 1)
                    }
                    mGroupDataBase.child(mGroupID + "/" + Constant.FIREBASE_UNREAD_MESSAGES_COUNT)
                        .child("user_" + App.userBean?.id.toString())
                        .setValue(0)
                }

                val str = mGroupID
                val parts = str.split("_")
                val partsOne = parts[0]
                val partsTwo = parts[1]

                if (!partsOne.equals(App.userBean?.id.toString())) {
                    user_id = partsOne
                }

                if (!partsTwo.equals(App.userBean?.id.toString())) {
                    user_id = partsTwo
                }

                mGroupDataBase.child(mGroupID + "/" + Constant.FIREBASE_GROUP_MEMBERS)
                    .child("user_" + App.userBean?.id.toString()).child("is_archived").setValue(0)
                mGroupDataBase.child(mGroupID + "/" + Constant.FIREBASE_GROUP_MEMBERS)
                    .child("user_$user_id").child("is_archived").setValue(0)

            }
        }
    }


    /*
    Fetch group info
     */

    var mMemberMap: Map<Any, Any> = HashMap()

    private fun fetchGroupInfo() {
        val queryGroupData = mGroupDataBase.child(mGroupID + "/" + Constant.FIREBASE_GROUP_INFO)
        queryGroupData.addListenerForSingleValueEvent(object : EventListener, ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val map = dataSnapshot.value as Map<*, *>
                val group_image = map["group_image"].toString()
                val group_name = map["group_name"].toString()
                mGroupAdminID = map["group_admin_id"].toString()
                mGroupImage = group_image
                mGroupName = group_name
                Glide.with(mActivity!!).load(group_image).centerCrop().into(mBinding.imgGroup)
                mBinding.tvGroupTitle.text = group_name

            }
        })
    }

    /*
    Fetch Member MetaData
     */
    private fun fetchMemberMetadata() {
        val queryMemberCount =
            mGroupDataBase.child(mGroupID + "/" + Constant.FIREBASE_GROUP_MEMBERS)
        queryMemberCount.addListenerForSingleValueEvent(object : EventListener, ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        val map = dataSnapshot.value as Map<*, *>
                        mMemberMap = map as Map<Any, Any>
                        if (mIsGroup == "1") {
                        } else {
                            setPersonalChatInfo()
                        }
//                        val queryMemberCount = dataSnapshot.value.

                    }
                } catch (e: Exception) {
                }
            }
        })
    }

    /*
    set listeners
     */

    override fun setOnClickListener() {
        mBinding.rlBack.setOnClickListener(this)
        mBinding.imgSend.setOnClickListener(this)
//        mBinding.imgAttechement.setOnClickListener(this)
        mBinding.txtBlock.setOnClickListener(this)
        mBinding.imgMore.setOnClickListener(this)
        mBinding.imgGroup.setOnClickListener(this)
        mBinding.tvGroupTitle.setOnClickListener(this)
        mBinding.layoutProfile.setOnClickListener(this)

        mBinding.recyclerChatList.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    //KeyboardUtil.hideKeyboard(this)
                }//Do Something
            }
            v?.onTouchEvent(event) ?: true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        val str = mGroupID
        val parts = str.split("_")
        val partsOne = parts[0]
        val partsTwo = parts[1]
        if (!partsOne.equals(App.userBean?.id.toString())) {
            user_id = partsOne
        }
        if (!partsTwo.equals(App.userBean?.id.toString())) {
            user_id = partsTwo
        }
        when (v!!.id) {
            R.id.imgGroup -> {
                openActivity<ViewActivity>(
                    Constants.PROFILE_FLAG to Constants.PROFILE_FLAG,
                    Constants.PROFILE to user_id
                )
            }
            R.id.layoutProfile -> {
                openActivity<ViewActivity>(
                    Constants.PROFILE_FLAG to Constants.PROFILE_FLAG,
                    Constants.PROFILE to user_id
                )
            }
            R.id.rlBack -> {
                onBackPressed()
            }

            R.id.imgSend -> {
                if (isValidFormData(mBinding.edtMessage.text.toString())) {
                    if (NetworkUtil.isNetworkAvailable(this)) {
                        sendMessage(mBinding.edtMessage.text.toString(), "text")
                    } else {
                        Snackbar.make(
                            mBinding.imgSend,
                            mActivity!!.getString(R.string.error_internet_connection),
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction(mActivity!!.getString(R.string.retry)) {
                            mBinding.imgSend.performClick()
                        }.show()
                    }
                }
            }
        }
    }

    private fun isValidFormData(address: String): Boolean {
        if (TextUtils.isEmpty(address)) {
            toastError("Please enter message")
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendMessage(message: String, messageType: String) {
        var metaDataKey: DatabaseReference? = null
        mBinding.edtMessage.setText("")
        val messgaesKey = mMessagesDataBase.child(mGroupID).push()
        metaDataKey = mGroupDataBase.child(mGroupID)

        /*Query for set message into new node of firebase database*/
        val senderName = App.userBean?.name.toString()
        val push_id = messgaesKey.key
        val req = HashMap<String, Any>()
        var msg = encoder(message)
        req["message"] = msg
        req["sender_id"] = App.userBean?.id.toString()
        req["sender_name"] = senderName
        req["sender_image"] = ""
        req["time"] = ServerValue.TIMESTAMP
        req["dateTS"] = ServerValue.TIMESTAMP
        req["Common_message_hname"] = senderName
        req["messages_type"] = messageType
        val messageUserMap = HashMap<String, Any>()
        messageUserMap["${Constant.FIREBASE_MESSAGES_TABLE + "/$mGroupID"}/$push_id"] = req

        FirebaseDatabase.getInstance().reference.updateChildren(messageUserMap) { databaseError, databaseReference ->
            if (databaseError != null) {
            } else {
            }
        }
        /*Query for set unread message count*/
        mGroupDataBaseReferenceListener = object : EventListener,
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get unread message count row one by one and set the unread message count in this.
                try {
                    if (dataSnapshot.exists()) {
                        for (mapUnreadMessageCount in dataSnapshot.children) {
                            if (mapUnreadMessageCount.key != "user_" + App.userBean?.id.toString()
                            ) {
                                metaDataKey.child(Constant.FIREBASE_UNREAD_MESSAGES_COUNT)
                                    .child(mapUnreadMessageCount.key.toString())
                                    .setValue(if (mapUnreadMessageCount.value == null) 0 else mapUnreadMessageCount.value as Long + 1)
                            } else {
                            }
                        }
                    }
                } catch (e: Exception) {
                }
            }
        }
        mGroupDataBase.child(mGroupID)
            .child(Constant.FIREBASE_UNREAD_MESSAGES_COUNT)
            .addListenerForSingleValueEvent(mGroupDataBaseReferenceListener!!)

        /*This one for set the last message data in firebase database*/
        val reqLastMessage = HashMap<String, Any>()
        reqLastMessage["message"] = encoder(message)
        reqLastMessage["id"] = App.userBean?.id.toString()
        reqLastMessage["name"] = senderName
        reqLastMessage["image"] = ""
        reqLastMessage["messages_type"] = messageType
        reqLastMessage["time"] = Date().time
        metaDataKey.child(Constant.FIREBASE_LAST_MESSAGES_DETAILS).setValue(reqLastMessage)
        if (messageType.equals("text")) {
            hitMessageNotificationRequestAPI(message)
        } else if (messageType.equals("image")) {
//            hitMessageNotificationRequestAPI(getString(R.string.sent_you_image))
        }
    }

    fun getDate(time: String): String {
        val cal = Calendar.getInstance(Locale.getDefault())
        cal.timeInMillis = java.lang.Long.parseLong(time)
        return DateFormat.format("HH:mm", cal).toString()
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
            var timeNew: String = Utility.getFormattedDateNotification(this, time)!!
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

    override fun onDestroy() {
        super.onDestroy()
        try {
            mChatAdapter!!.mediaPlayer!!.stop()
            mChatAdapter!!.mediaPlayer!!.release()
            exitProcess(0)
        } catch (e: Exception) {
        }

        if (messageReferenceListender != null) {
            mMessagesDataBase.child(mGroupID).removeEventListener(messageReferenceListender!!)
            mMessagesDataBase.removeEventListener(messageReferenceListender!!)
        }
        if (mGroupDataBaseReferenceListener != null) {
            mGroupDataBase.removeEventListener(mGroupDataBaseReferenceListener!!)
        }
        mMessagesDataBase.onDisconnect()
        mMessagesDataBase.ref.onDisconnect()
        mGroupDataBaseReferenceListener = null
        messageReferenceListender = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun encoder(filePath: String): String {
        try {
            val encodedString: String = Base64.getEncoder().encodeToString(filePath.toByteArray())
            return encodedString
        } catch (e: Exception) {
        }
        return ""
    }

    override fun onBackPressed() {
        try {
            mChatAdapter!!.mediaPlayer!!.stop()
            mChatAdapter!!.mediaPlayer!!.release()
            exitProcess(0)
        } catch (e: Exception) {
        }
        if (messageReferenceListender != null) {
            mMessagesDataBase.child(mGroupID).removeEventListener(messageReferenceListender!!)
            mMessagesDataBase.removeEventListener(messageReferenceListender!!)
        }
        if (mGroupDataBaseReferenceListener != null) {
            mGroupDataBase.removeEventListener(mGroupDataBaseReferenceListener!!)
        }
        mMessagesDataBase.onDisconnect()
        mMessagesDataBase.ref.onDisconnect()
        mGroupDataBaseReferenceListener = null
        messageReferenceListender = null
        finish()
    }

    private fun hitMessageNotificationRequestAPI(message: String) {
        val str = mGroupID
        val parts = str.split("_")
        val partsOne = parts[0]
        val partsTwo = parts[1]
        if (!partsOne.equals(App.userBean?.id.toString())) {
            user_id = partsOne
        }
        if (!partsTwo.equals(App.userBean?.id.toString())) {
            user_id = partsTwo
        }
        viewModel.stUserID.set(user_id)
        viewModel.stMessage.set(message)
        viewModel.callSendNotificationApi()
    }

    private fun registerObservers() {
        viewModel.response.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {}
                is NetworkResult.Success<*> -> {
                    response.data?.result?.let {
                    }
                }
                is NetworkResult.Error -> {
                    toastError(response.exception.localizedMessage!!.toString())
                }
            }
        }
    }

    private fun getObserver() {
        viewModel.responseUser.observe(this) { response ->
            when (response) {
                is NetworkResult.Loading -> {
                    ShowProgressDialog.showProgress(this)
                }
                is NetworkResult.Success<*> -> {
                    ShowProgressDialog.hideProgress()
                    response.data?.result?.let {
                        tryCast<UserDetailsModel>(it) {
                            setData(this.userDetails)

                        }
                    }
                }
                is NetworkResult.Error -> {
                    ShowProgressDialog.hideProgress()
                    toastError(response.exception.localizedMessage!!.toString())
                }
            }
        }
    }

    private fun setData(data: UserDetailsModel.UserDetails?) {
        data?.let {
            if (data.isChat == true) {
                mBinding.rlMessage.visible()
            } else {
                mBinding.rlMessage.gone()
            }
        }
    }

    private fun initFirebase(firebaseID: String) {
        try {
            App.mUserDatabase =
                FirebaseDatabase.getInstance().reference.child(Constant.FIREBASE_USER_TABLE)
                    .child(firebaseID)
            App.mUserDatabase!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot != null) {
                        if (dataSnapshot.hasChild("timestamp")) {
                            val onlineTimeStatus = dataSnapshot.child("timestamp").value.toString()
                            Log.d("TAGtimestmap", onlineTimeStatus)
                            Log.d(
                                "TAGabcd",
                                ((System.currentTimeMillis() - onlineTimeStatus.toLong()) > 60000).toString()
                            ).toString()
                            if (System.currentTimeMillis() - onlineTimeStatus.toLong() > 60000) {
                                mBinding.tvGroupDate.text =
                                    "Last seen " + getDateTime(onlineTimeStatus.toLong())
                            } else {
                                mBinding.tvGroupDate.text = "Online"
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        } catch (e: Exception) {
        }
    }

}