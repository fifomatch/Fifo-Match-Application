package com.fifo.match.ui.activity.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fifo.match.R
import com.fifo.match.databinding.ItemSaveBinding
import com.fifo.match.model.LikeSavedModel
import com.fifo.match.ui.base.BaseViewHolder
import com.fifo.match.utils.extensions.gone
import com.fifo.match.utils.extensions.visible
import com.github.hariprasanths.bounceview.BounceView

internal class SaveAdapter(private val listSave: ArrayList<LikeSavedModel.Data>,private val getClick:GetClick) : RecyclerView.Adapter<SaveAdapter.NotificationViewHolder>() {

     inner class NotificationViewHolder(binding: ItemSaveBinding) :
        BaseViewHolder<ItemSaveBinding>(binding) {
            fun bind(data: LikeSavedModel.Data, position: Int) {
                BounceView.addAnimTo(binding.icSave)
                binding.apply {
                    Glide.with(itemView.context).load(data.user!!.photo!!.name).into(tvProfileImage)
                    tvName.text = data.user.name + " " + data.user.age
                    tvCountryName.text = data.user.countryName.toString()
                    icSave.setOnClickListener {
                        getClick.onSavedClick(data.user.id!!.toInt(),position)
                    }
                    tvProfileImage.setOnClickListener {
                        getClick.onProfileClick(data.savedUserId!!.toInt(),position)
                    }
                    if (data.userVerified != null){
                        if (data.userVerified.isAccepted == true) icVerify.visible() else icVerify.gone()
                    }
                }

            }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_save, parent, false))
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(listSave[position],position)
    }

    override fun getItemCount(): Int {
        return listSave.size
    }

    interface GetClick{
        fun onSavedClick(id: Int,position: Int)
        fun onProfileClick(id: Int,position: Int)
    }

}