package com.fifo.match.ui.activity.chat

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.fifo.match.App
import com.fifo.match.R
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(var mContext: Context?, var mChatsList: ArrayList<Chat>, var user_name: String, var user_image: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var circularProgressDrawable = CircularProgressDrawable(mContext!!)
    var mediaPlayer: MediaPlayer? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        mContext = parent.context
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            VIEW_TYPE_ME -> {
                val viewChatMine =
                    layoutInflater.inflate(R.layout.layout_chat_right_view, parent, false)
                viewHolder = MyChatViewHolder(viewChatMine)
            }
            VIEW_TYPE_OTHER -> {
                val viewChatOther =
                    layoutInflater.inflate(R.layout.layout_chat_left_view, parent, false)
                viewHolder = OtherChatViewHolder(viewChatOther)
            }
        }
        return viewHolder!!
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            if (mChatsList[position].receiver_id == App.userBean!!.id.toString()) {
                configureMyChatViewHolder(holder as MyChatViewHolder, position)
            } else {
                configureOtherChatViewHolder(holder as OtherChatViewHolder, position, user_image, user_name)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureMyChatViewHolder(myChatViewHolder: MyChatViewHolder, position: Int) {
        when {
            mChatsList[position].messageType.equals("text", true) -> {
                myChatViewHolder.txtChatMessage.visibility = View.VISIBLE
                myChatViewHolder.imgAttechement.visibility = View.GONE

                myChatViewHolder.txtChatMessage.text = decoder(mChatsList[position].message)
            }

            mChatsList[position].messageType.equals("image", true) -> {
                myChatViewHolder.txtChatMessage.visibility = View.GONE
                myChatViewHolder.imgAttechement.visibility = View.VISIBLE
                circularProgressDrawable.strokeWidth = 8f
                circularProgressDrawable.centerRadius = 50f
                circularProgressDrawable.start()
                Glide.with(mContext!!)
                    .load(decoder(mChatsList[position].message))
                    .apply(
                        RequestOptions.placeholderOf(circularProgressDrawable).fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    )
                    .into(myChatViewHolder.imgAttechement)
            }
        }

        val thisDate: String = getDateHolder(mChatsList[position].timestamp.toLong())!!
        myChatViewHolder.mDateContainer.text = thisDate

        var prevDate: String? = null

        // get previous item's date, for comparison

        // get previous item's date, for comparison
        if (position > 0) {
            prevDate = getDateHolder(mChatsList[position - 1].timestamp.toLong())!!
        }

        // enable section heading if it's the first one, or
        // different from the previous one

        // enable section heading if it's the first one, or
        // different from the previous one
        if (prevDate == null || prevDate != thisDate) {
            myChatViewHolder.mDateContainer.visibility = View.VISIBLE
        } else {
            myChatViewHolder.mDateContainer.visibility = View.GONE
        }

        myChatViewHolder.senderMsgTime.text = getDate(mChatsList[position].timestamp)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureOtherChatViewHolder(otherChatViewHolder: OtherChatViewHolder, position: Int, user_image: String, user_name: String) {
        try {
            Glide.with(mContext!!).load(user_image)
                .apply(
                    RequestOptions().placeholder(R.drawable.ic_bottom_tab_myprofile)
                        .error(R.drawable.ic_bottom_tab_myprofile)
                )
                .into(otherChatViewHolder.imgUserProfile)
        } catch (e: Exception) {
        }
        when {
            mChatsList[position].messageType.equals("text", true) -> {
                otherChatViewHolder.txtChatMessage.visibility = View.VISIBLE
                otherChatViewHolder.imgAttechement.visibility = View.GONE
                otherChatViewHolder.txtChatMessage.text = decoder(mChatsList[position].message)
            }

            mChatsList[position].messageType.equals("image", true) -> {
                otherChatViewHolder.txtChatMessage.visibility = View.GONE
                otherChatViewHolder.imgAttechement.visibility = View.VISIBLE
                circularProgressDrawable.strokeWidth = 8f
                circularProgressDrawable.centerRadius = 50f
                circularProgressDrawable.start()
                Glide.with(mContext!!)
                    .load(decoder(mChatsList[position].message))
                    .apply(
                        RequestOptions.placeholderOf(circularProgressDrawable).fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    )
                    .into(otherChatViewHolder.imgAttechement)
            }
        }

        val thisDate: String = getDateHolder(mChatsList[position].timestamp.toLong())!!
        otherChatViewHolder.mDateContainer.text = thisDate

        var prevDate: String? = null

        // get previous item's date, for comparison

        // get previous item's date, for comparison
        if (position > 0) {
            prevDate =
                getDateHolder(mChatsList[position - 1].timestamp.toLong())!!
        }

        // enable section heading if it's the first one, or
        // different from the previous one

        // enable section heading if it's the first one, or
        // different from the previous one
        if (prevDate == null || prevDate != thisDate) {
            otherChatViewHolder.mDateContainer.visibility = View.VISIBLE
        } else {
            otherChatViewHolder.mDateContainer.visibility = View.GONE
        }

        otherChatViewHolder.receiverMsgTime.text = getDate(mChatsList[position].timestamp)
        otherChatViewHolder.tvNameCode.text = user_name

    }

    override fun getItemCount(): Int {
        return mChatsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mChatsList[position].receiver_id == App.userBean!!.id.toString()) {
            VIEW_TYPE_ME
        } else {
            VIEW_TYPE_OTHER
        }
    }

    fun updateData(mChatsList: ArrayList<Chat>) {
        this.mChatsList = mChatsList
        notifyDataSetChanged()
    }


    private class MyChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtChatMessage: AppCompatTextView = itemView.findViewById(R.id.tvMessage)
        val senderMsgTime: AppCompatTextView = itemView.findViewById(R.id.tvTime)
        val imgAttechement: AppCompatImageView = itemView.findViewById(R.id.imgAttechement)
        val mDateContainer: AppCompatTextView = itemView.findViewById(R.id.mDateContainerView)

    }

    private class OtherChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtChatMessage: AppCompatTextView = itemView.findViewById(R.id.tvMessage)
        val receiverMsgTime: AppCompatTextView = itemView.findViewById(R.id.tvTime)
        val tvNameCode: AppCompatTextView = itemView.findViewById(R.id.tvNameCode)
        var imgUserProfile: CircleImageView = itemView.findViewById(R.id.imgUser)
        val imgAttechement: AppCompatImageView = itemView.findViewById(R.id.imgAttechement)
        val mDateContainer: AppCompatTextView = itemView.findViewById(R.id.mDateContainerView)

    }

    fun getDateHolder(timestamp: Long): String? {
        return try {
            // SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy,hh:mm aa");
            val sdf1 = SimpleDateFormat("dd MMM yyyy")
            // sdf1.timeZone = TimeZone.getTimeZone("UTC")
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
        } catch (ex: java.lang.Exception) {
            "xx"
        }
    }

    companion object {

        private val VIEW_TYPE_ME = 1
        private val VIEW_TYPE_OTHER = 2
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decoder(base64Str: String): String {
        var decodedString: String = ""
        try {

            val decodedBytes = Base64.getDecoder().decode(base64Str)
            decodedString = String(decodedBytes)

        } catch (e: Exception) {
            decodedString = ""
        }

        return decodedString
    }

    fun getDate(time: String): String {
        val cal = Calendar.getInstance(Locale.getDefault())
        cal.timeInMillis = java.lang.Long.parseLong(time)
        return DateFormat.format("HH:mm", cal).toString()
    }
}