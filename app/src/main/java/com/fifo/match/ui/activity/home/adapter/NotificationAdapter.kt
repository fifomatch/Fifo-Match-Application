package com.fifo.match.ui.activity.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fifo.match.R
import com.fifo.match.databinding.ItemNotifiBinding
import com.fifo.match.model.NotificationModel
import com.fifo.match.ui.base.BaseViewHolder
import com.fifo.match.utils.extensions.getDateTime

internal class NotificationAdapter(
    private val listNotification: ArrayList<NotificationModel>,
    private val onClick: GetOnClick
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(binding: ItemNotifiBinding) :
        BaseViewHolder<ItemNotifiBinding>(binding) {

        fun bind(data: NotificationModel) {
            binding.apply {
                data.let {
                    tvNameMatch.text = it.title
                    tvDesc.text = it.subtitle
//                    tvTime.text = it.createdAt
                    Glide.with(itemView.context).load(it.photo?.name).into(profileImage)
                    clLayout.setOnClickListener {
                        if (data.openId != null) {
                            onClick.onMatchType(data.type.toString(), data.openId.toInt())
                        } else {
                            onClick.onLikeType(data.type.toString(), data.fromId!!.toInt())
                        }
                    }
                    val currentString = data.createdAt
                    val separated = currentString?.split("T")!!.toTypedArray()
                    val dd =    separated[0]
//                    val setdate =  getDateTime(itemView.context,dd.toLong())
//                    Log.d("TAGset", setdate.toString())
                    tvTime.text =dd
                }

            }
        }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_notifi, parent, false))
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(listNotification[position])
    }

    override fun getItemCount(): Int {
        return listNotification.size
    }

    interface GetOnClick {
        fun onLikeType(type: String, id: Int)
        fun onMatchType(type: String, openId: Int)
    }

}