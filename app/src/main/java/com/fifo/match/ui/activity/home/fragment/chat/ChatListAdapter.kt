package com.fifo.match.ui.activity.home.fragment.chat

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.fifo.match.App
import com.fifo.match.R
import com.fifo.match.databinding.LayoutHomeFragmentItemsBinding
import com.fifo.match.ui.activity.chat.ChatStartActivity
import com.fifo.match.ui.activity.chat.Constant
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.math.abs

class ChatListAdapter(var mDataList: ArrayList<GroupMetaData>?, activity: FragmentActivity,private val getCLick: GetCLick) : RecyclerBaseAdapter() {

    var mContextCompat = activity

    override fun getLayoutIdForPosition(position: Int) = R.layout.layout_home_fragment_items
    override fun getViewModel(position: Int) = mDataList!![position]

    @RequiresApi(Build.VERSION_CODES.O)
    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        if (viewDataBinding is LayoutHomeFragmentItemsBinding) {
            viewDataBinding.position = position
            Glide.with(viewDataBinding.groupImage)
                .load(mDataList?.get(position)?.group_image)
                .placeholder(R.drawable.ic_my_profile)
                .error(R.drawable.ic_my_profile)
                .into(viewDataBinding.groupImage)

            viewDataBinding.tvMessageTime.text = "\u2022"+" "+mDataList!![position].time
            viewDataBinding.groupName.text = mDataList!![position].group_name
            viewDataBinding.tvCounts.text = mDataList!![position].mMessageCount

             initFirebase(mDataList!![position].firebase_id, viewDataBinding)

            when (mDataList!![position].messages_type) {
                "text" -> {
                    viewDataBinding.tvMessage.text = decoder(mDataList!![position].mLastMessage)
                    viewDataBinding.tvMessage.visibility = View.VISIBLE
                    viewDataBinding.imgMedia.visibility = View.GONE
                    viewDataBinding.imgMedia1.visibility = View.GONE
                }
                "image" -> {
                    viewDataBinding.tvMessage.visibility = View.GONE
                    viewDataBinding.imgMedia.visibility = View.VISIBLE
                    viewDataBinding.imgMedia1.visibility = View.GONE
                    viewDataBinding.imgMedia.setBackgroundResource(R.drawable.gallery)
                }
            }

            if (checkCount(mDataList!![position].mMessageCount)) {
                viewDataBinding.tvCounts.visibility = View.VISIBLE
                viewDataBinding.tvMessage.setTextColor(mContextCompat.resources.getColor(R.color.black))

            } else {
                viewDataBinding.tvCounts.visibility = View.GONE
                viewDataBinding.tvMessage.setTextColor(mContextCompat.resources.getColor(R.color.text_color6))
            }

            viewDataBinding.layDelete.setOnClickListener{
                viewDataBinding.linerLayoutParent.close(true)
                getCLick.getChatDelete(position,mDataList!![position].group_id!!)
                mDataList!!.removeAt(position)
                notifyItemRemoved(position)
            }

            viewDataBinding.layArchived.setOnClickListener{
                viewDataBinding.linerLayoutParent.close(true)
                archivedChat(position)
            }


            viewDataBinding.frameLayout.setOnClickListener {
                val i = Intent(mContextCompat, ChatStartActivity::class.java)
                i.putExtra("groupID", mDataList?.get(position)?.group_id)
                i.putExtra("isGroup", mDataList?.get(position)?.isGroup)
                i.putExtra("time", mDataList!![position].timestamp.toString())
                i.putExtra("firebaseID", mDataList!![position].firebase_id.toString())
                mContextCompat.startActivity(i)
            }

        }
    }

    private fun archivedChat(pos: Int) {
        val mID = mDataList!![pos].group_id.split("_")
        var mOtherUserID1 = ""
        mID.indices.forEach { i ->
            if (App.userBean!!.id.toString() != mID[i]) {
                mOtherUserID1 = mID[i]
            }
        }
        val messageMap = HashMap<String, Any>()
        messageMap["is_archived"] = 1
        val mGroupDataBase = FirebaseDatabase.getInstance().reference.child(Constant.FIREBASE_GROUP_TABLE)
        mGroupDataBase.child(mDataList!![pos].group_id).child("/group_member/").child("user_$mOtherUserID1").updateChildren(messageMap)
        mDataList!!.removeAt(pos)
        notifyDataSetChanged()

    }

    private fun checkCount(count: String): Boolean {
        return when {
            count.isEmpty() -> {
                false
            }
            count == "0" -> {
                false
            }
            else -> {
                true
            }
        }
    }

    override fun getItemCount() = if (mDataList.isNullOrEmpty()) 0 else mDataList!!.size

    fun updateDataModel(mGroupMetaDataList: ArrayList<GroupMetaData>,updateMainList:(ArrayList<GroupMetaData>)->Unit) {
        val chatList= arrayListOf<GroupMetaData>()
        mGroupMetaDataList.forEach {
            if(it.group_name.trim().isNotEmpty() && it.is_archived=="0")
                chatList.add(it)
        }

        mDataList = chatList
        updateMainList.invoke(chatList)
        notifyDataSetChanged()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decoder(base64Str: String): String {
        var decodedString: String
        try {

            val decodedBytes = Base64.getDecoder().decode(base64Str)
            decodedString = String(decodedBytes)

        } catch (e: Exception) {
            decodedString = ""
        }

        return decodedString
    }

    fun deleteData(poss: Int) {
        mDataList!!.removeAt(poss)
        if (mDataList!!.size>0){
            notifyItemRemoved(poss)
        }else{
            notifyDataSetChanged()
        }
    }

    private fun initFirebase(firebaseID:String, viewDataBinding: LayoutHomeFragmentItemsBinding)  {
        Log.d("TAGfirebaseid", firebaseID)
        try {
           App.mUserDatabase  = FirebaseDatabase.getInstance().reference.child(Constant.FIREBASE_USER_TABLE).child(firebaseID)
            App.mUserDatabase!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot != null) {
                        if (dataSnapshot.hasChild("timestamp")) {
                            val  onlineTimeStatus = dataSnapshot.child("timestamp").value.toString()
                            Log.d("TAGtimesheet", onlineTimeStatus)
                            if (viewDataBinding is LayoutHomeFragmentItemsBinding){
                                if (abs(System.currentTimeMillis()- onlineTimeStatus.toLong()) > 60000){
                                    viewDataBinding.rvOnline.visibility = View.GONE
                                } else {
                                    viewDataBinding.rvOnline.visibility = View.VISIBLE
                                }
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

    interface GetCLick {
        fun getChatDelete(position: Int,int: String)
    }
}