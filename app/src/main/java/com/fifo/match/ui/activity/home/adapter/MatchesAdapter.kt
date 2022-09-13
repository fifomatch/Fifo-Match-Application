package com.fifo.match.ui.activity.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fifo.match.R
import com.fifo.match.`interface`.GetOnClick
import com.fifo.match.databinding.ItemOnlineBinding
import com.fifo.match.model.MatchListModel
import com.fifo.match.ui.base.BaseViewHolder
import com.fifo.match.utils.extensions.gone
import com.fifo.match.utils.extensions.visible
import com.github.hariprasanths.bounceview.BounceView

internal class MatchesAdapter(private val listOnline: ArrayList<MatchListModel.User>, private val onItemClick: GetOnClick) : RecyclerView.Adapter<MatchesAdapter.NotificationViewHolder>() {

    internal inner class NotificationViewHolder(binding: ItemOnlineBinding) :
        BaseViewHolder<ItemOnlineBinding>(binding) {

            fun bind(data: MatchListModel.User, position: Int) {
                BounceView.addAnimTo(binding.icCancel)
                BounceView.addAnimTo(binding.icLike)
                BounceView.addAnimTo(binding.icSave)
                binding.apply {
                    tvName.text = data.name
                    tvAge.text = data.age.toString()
                    tvLocation.text = data.countryName.toString()
                    Glide.with(itemView.context).load(data.photo?.name).into(tvProfileImage)

                    clLayout.setOnClickListener {
                        onItemClick.onProfileClick(data.id!!.toInt(),position)
                    }
                    icCancel.setOnClickListener {
                        onItemClick.onCancelClick(data.id!!.toInt(),position)
                    }

                    icLike.setOnClickListener {
                        onItemClick.onLikeClick(data.id!!.toInt(),position)
                    }

                    icSave.setOnClickListener {
                        onItemClick.onSaveClick(data.id!!.toInt(),position)
                    }

                    if (data.userVerified != null){
                        if (data.userVerified.isAccepted == true){
                            icVerify.visible()
                        }else{
                            icVerify.gone()
                        }
                    }

                }
            }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_online, parent, false))
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(listOnline[position],position)
    }

    override fun getItemCount(): Int {
        return listOnline.size
    }

}