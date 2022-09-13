package com.fifo.match.ui.activity.hiddenProfile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fifo.match.R
import com.fifo.match.databinding.ItemHiddenPhotoBinding
import com.fifo.match.model.LikeSavedModel
import com.fifo.match.ui.base.BaseViewHolder
import com.fifo.match.utils.extensions.gone
import com.fifo.match.utils.extensions.visible

internal class HiddenAdapter(private val listHidden: ArrayList<LikeSavedModel.Data>, private val getClick: GetClick) : RecyclerView.Adapter<HiddenAdapter.NotificationViewHolder>() {

    internal inner class NotificationViewHolder(binding: ItemHiddenPhotoBinding) : BaseViewHolder<ItemHiddenPhotoBinding>(binding) {
        fun bind(data: LikeSavedModel.Data, position: Int) {
            binding.apply {
                Glide.with(itemView.context).load(data.user?.photo!!.name).into(tvProfileImage)
                tvName.text = data.user.name + " " + data.user.age
                tvOcc.text = data.user.countryName.toString()
                scSwitch.setOnClickListener {
                    getClick.onButtonClick(data.user.id!!.toInt(),position)
                }
                tvProfileImage.setOnClickListener {
                    getClick.onProfileClick(data.user.id!!.toInt(),position)
                }
                if (data.userVerified != null){
                    if (data.userVerified.isAccepted == true) icVerify.visible() else icVerify.gone()
                }
            }
        }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_hidden_photo, parent, false))
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(listHidden[position],position)
    }

    override fun getItemCount(): Int {
        return listHidden.size
    }

    interface GetClick{
        fun onButtonClick(id: Int,position: Int)
        fun onProfileClick(id: Int,position: Int)
    }

}