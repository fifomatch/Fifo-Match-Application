package com.fifo.match.ui.activity.completeProfile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fifo.match.R
import com.fifo.match.databinding.ItemImageBinding
import com.fifo.match.ui.base.BaseViewHolder
import com.fifo.match.utils.extensions.gone
import com.fifo.match.utils.extensions.visible
import java.io.File
import java.util.*

internal class ImageAdapter(private val listUri: ArrayList<File?>, private val getClick: GetClick) :
    RecyclerView.Adapter<ImageAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(binding: ItemImageBinding) : BaseViewHolder<ItemImageBinding>(binding) {
        fun bind(file: File?, position: Int) {
            if (file == null) {
                binding.imageview.gone()
                binding.ivCancel.gone()
                Glide.with(itemView.context).load(R.drawable.ic_plus).into(binding.addImage)
            } else {
                binding.imageview.visible()
                binding.ivCancel.visible()
                Glide.with(itemView.context).load(file).into(binding.imageview)
            }

            binding.ivCancel.setOnClickListener {
                getClick.getClick(position)
                binding.imageview.setImageDrawable(null)
                binding.ivCancel.gone()

            }
        }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_image, parent, false))
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(listUri[position],position)
    }

    override fun getItemCount(): Int {
        return listUri.size
    }

    fun getPhotos(): List<File> {
        return listUri.filterNotNull()
    }

    interface GetClick{
        fun getClick(int: Int)
    }

}