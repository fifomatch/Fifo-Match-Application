package com.fifo.match.ui.activity.viewYourProfile

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

internal class ViewYourProfileAdapter(private val listView: ArrayList<LikeSavedModel.Data>, private val getClick: GetClick) : RecyclerView.Adapter<ViewYourProfileAdapter.NotificationViewHolder>() {

    internal inner class NotificationViewHolder(binding: ItemSaveBinding) :
        BaseViewHolder<ItemSaveBinding>(binding) {
        fun bind(data: LikeSavedModel.Data) {
            binding.apply {
                icSave.gone()
                data.let { its->
                    if (its.user?.photo?.name != null) {
                        Glide.with(itemView.context).load(its.user.photo.name).into(tvProfileImage)
                    }
                    if (its.user?.name != null && its.user.age != null) {
                        tvName.text = its.user.name + " " + its.user.age
                    }
                    if (its.user?.countryName != null) {
                        tvCountryName.text = its.user.countryName.toString()
                    }
                    tvProfileImage.setOnClickListener {
                        getClick.onProfileClick(its.user?.id!!.toInt(), absoluteAdapterPosition)
                    }
                    if (data.userVerified != null){
                        if (data.userVerified.isAccepted == true) icVerify.visible() else icVerify.gone()
                    }
                }

            }

        }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_save, parent, false))
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(listView[position])
    }

    override fun getItemCount(): Int {
        return listView.size
    }

    interface GetClick {
        fun onProfileClick(id: Int, position: Int)
    }

}