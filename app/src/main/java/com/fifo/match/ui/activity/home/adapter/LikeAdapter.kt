package com.fifo.match.ui.activity.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fifo.match.App
import com.fifo.match.R
import com.fifo.match.databinding.ItemLikesBinding
import com.fifo.match.model.LikeNewModel
import com.fifo.match.ui.base.BaseViewHolder
import com.github.hariprasanths.bounceview.BounceView

internal class LikeAdapter(private val listSave: ArrayList<LikeNewModel>, private val getClick:GetClick) : RecyclerView.Adapter<LikeAdapter.NotificationViewHolder>() {

     inner class NotificationViewHolder(binding: ItemLikesBinding) :
        BaseViewHolder<ItemLikesBinding>(binding) {
            @SuppressLint("SetTextI18n")
            fun bind(data: LikeNewModel) {
                BounceView.addAnimTo(binding.icSave)
                binding.apply {

                    data.let {
                        if (App.userBean?.id.toString() == data.sender?.id.toString()) {
                            Glide.with(itemView.context).load(it.receiver?.photo?.name)
                                .into(tvProfileImage)
                            tvName.text = it.receiver?.name + " " + it.receiver?.age
                            tvCountryName.text = it.receiver?.occupation?.name

                            icSave.setOnClickListener {
                                getClick.onMessageClick(data.receiver?.id!!.toInt())
                            }
                            tvProfileImage.setOnClickListener {
                                getClick.onProfileClick(data.receiver?.id!!.toInt())
                            }

                        } else {
                            Glide.with(itemView.context).load(it.sender?.photo?.name).into(tvProfileImage)
                            tvName.text = it.sender?.name + " " + it.sender?.age
                            tvCountryName.text = it.sender?.occupation?.name

                            icSave.setOnClickListener {
                                getClick.onMessageClick(data.sender?.id!!.toInt())
                            }
                            tvProfileImage.setOnClickListener {
                                getClick.onProfileClick(data.sender?.id!!.toInt())
                            }
                        }

                    }
                }

            }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_likes, parent, false))
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(listSave[position])
    }

    override fun getItemCount(): Int {
        return listSave.size
    }

    interface GetClick{
        fun onProfileClick(id: Int)
        fun onMessageClick(id: Int)
    }

}