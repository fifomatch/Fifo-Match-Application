package com.fifo.match.ui.activity.chat

import android.content.Intent
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.fifo.match.R
import com.fifo.match.databinding.LayoutHomeFragmentItemsBinding
import com.fifo.match.ui.activity.home.fragment.chat.GroupMetaData
import com.fifo.match.ui.activity.home.fragment.chat.RecyclerBaseAdapter
import java.util.*
import kotlin.collections.ArrayList

class ArchivedChatListAdapter(
    var mDataList: ArrayList<GroupMetaData>?,
    activity: FragmentActivity
) : RecyclerBaseAdapter() {
    var mContextCompat = activity
    override fun getLayoutIdForPosition(position: Int) = R.layout.layout_home_fragment_items

    override fun getViewModel(position: Int) = mDataList!![position]

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        if (viewDataBinding is LayoutHomeFragmentItemsBinding) {
            viewDataBinding.position = position

            viewDataBinding.linerLayoutParent.setLockDrag(true)
            Glide.with(viewDataBinding.groupImage)
                .load(mDataList?.get(position)?.group_image)
                .into(viewDataBinding.groupImage)


            viewDataBinding.tvMessageTime.text = mDataList!![position].time
            viewDataBinding.tvMessage.text = decoder(mDataList!![position].mLastMessage)
            viewDataBinding.groupName.text = mDataList!![position].group_name
            viewDataBinding.tvCounts.text = mDataList!![position].mMessageCount
            if (checkCount(mDataList!![position].mMessageCount)) {
                viewDataBinding.tvCounts.visibility = View.VISIBLE
            } else {
                viewDataBinding.tvCounts.visibility = View.GONE
            }

         /*   viewDataBinding.layDelete.setOnClickListener{
                viewDataBinding.linerLayoutParent.close(true)
                msContext.deleteChat(position)
            }*/
            viewDataBinding.layArchived.setOnClickListener{
                //msContext.archivedChat(position)
            }

            viewDataBinding.frameLayout.setOnClickListener {
                val i = Intent(mContextCompat, ChatStartActivity::class.java)
                i.putExtra("groupID", mDataList?.get(position)?.group_id)
                i.putExtra("isGroup", mDataList?.get(position)?.isGroup)
                mContextCompat.startActivity(i)
                mContextCompat.finish()
            }

        }
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
    fun updateDataModel(mGroupMetaDataList: ArrayList<GroupMetaData>) {
        mDataList = mGroupMetaDataList
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decoder(base64Str: String): String {
        var decodedString: String=""
        try {

            val decodedBytes = Base64.getDecoder().decode(base64Str)
            decodedString = String(decodedBytes)

        } catch (e: Exception) {
            decodedString = ""
        }

        return decodedString
    }
}