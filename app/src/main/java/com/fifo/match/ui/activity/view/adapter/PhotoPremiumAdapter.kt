package com.fifo.match.ui.activity.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fifo.match.R
import com.fifo.match.databinding.ItemPhotoBinding
import com.fifo.match.model.UserDetailsModel
import com.fifo.match.network.utils.Constants
import com.fifo.match.ui.activity.fullImage.ImageViewActivity
import com.fifo.match.ui.base.BaseViewHolder
import com.fifo.match.utils.extensions.gone
import com.fifo.match.utils.extensions.snack
import com.fifo.match.utils.extensions.visible


internal class PhotoPremiumAdapter(private val listPhotos: ArrayList<UserDetailsModel.UserDetails.Photo?>, private var isPremium: Boolean) : RecyclerView.Adapter<PhotoPremiumAdapter.NotificationViewHolder>() {

    internal inner class NotificationViewHolder(binding: ItemPhotoBinding) : BaseViewHolder<ItemPhotoBinding>(binding) {

        fun bind(data: UserDetailsModel.UserDetails.Photo?, isPremium: Boolean) {
            binding.apply {

                /*if (absoluteAdapterPosition < 3) {
                    binding.clLock.gone()
                    ivImage.isSelected = true
                } else {
                    if (!isPremium) {
                        clLock.visible()
                        ivImage.isSelected = false
                    }else{
                        ivImage.isSelected = true
                    clLock.gone()
                    }
                }*/

                Glide.with(itemView.context).load(data!!.name).into(ivImage)
                ivImage.setOnClickListener {
                    val intent = Intent(itemView.context, ImageViewActivity::class.java)
                    intent.putExtra(Constants.IMAGE_FLAG, data.name)
                    itemView.context.startActivity(intent);
                    /*if (ivImage.isSelected){
                        val intent = Intent(itemView.context, ImageViewActivity::class.java)
                        intent.putExtra(Constants.IMAGE_FLAG, data.name)
                        itemView.context.startActivity(intent);
                    }else{
                        ivImage.snack(R.string.purchase_plan_prmium,1000) { }
                    }*/
                }

            }
        }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_photo, parent, false))
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(listPhotos[position], isPremium)
    }

    override fun getItemCount(): Int {
        return listPhotos.size
    }

}