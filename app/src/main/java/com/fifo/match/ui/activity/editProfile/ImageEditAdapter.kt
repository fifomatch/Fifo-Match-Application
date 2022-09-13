package com.fifo.match.ui.activity.editProfile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fifo.match.R
import com.fifo.match.databinding.ItemImageBinding
import com.fifo.match.model.ProfileModel
import com.fifo.match.ui.base.BaseViewHolder
import com.fifo.match.utils.extensions.gone
import com.fifo.match.utils.extensions.visible

internal class ImageEditAdapter(private val listUri: ArrayList<ProfileModel.Photo>, private val getClick: GetClick) : RecyclerView.Adapter<ImageEditAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(binding: ItemImageBinding) :
        BaseViewHolder<ItemImageBinding>(binding) {

        fun bind(listPhoto: ArrayList<ProfileModel.Photo>, position: Int) {
            if (listPhoto.size > position) {

                if (!listPhoto[position].name.equals("") || listPhoto[position].name != null) {
                    Glide.with(itemView.context).load(listPhoto[position].name).into(binding.imageview)
                }

                if (listPhoto[position].nameFile != null) {
                    Glide.with(itemView.context).load(listPhoto[position].nameFile).into(binding.imageview)
                }

                binding.imageview.visible()
                binding.ivCancel.visible()
                binding.addImage.gone()
            } else {
                binding.imageview.gone()
                binding.ivCancel.gone()
                binding.addImage.visible()
                Glide.with(itemView.context).load(R.drawable.ic_plus).into(binding.addImage)
            }

            binding.addImage.setOnClickListener {
                getClick.pickImageClick(position)
            }

            binding.ivCancel.setOnClickListener {
                var count = 0
                listPhoto.forEachIndexed { index, photo ->
                    if (!listPhoto[index].name.equals("") && listPhoto[index].name != null) {
                        count++
                    }
                }
                if (listPhoto[position].name != null && listPhoto[position].name!!.isNotEmpty()) {
                    if (count > 3) {
                        getClick.getClick(listPhoto[position].id!!.toInt(), position, 1)
                        binding.imageview.setImageDrawable(null)
                        listPhoto.removeAt(position)
                        binding.ivCancel.gone()
                        binding.addImage.visible()
                        count--
                    } else {
                        getClick.getClick(position, position, 2)
                    }
                } else if (listPhoto[position].nameFile != null) {
                    listPhoto.removeAt(position)
                    binding.imageview.setImageDrawable(null)
                    binding.ivCancel.gone()
                    binding.addImage.visible()
                }
            }
        }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_image,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(listUri, position)

    }

    override fun getItemCount(): Int {
        return 9
    }

    interface GetClick {
        fun getClick(int: Int, position: Int, number: Int)
        fun pickImageClick(position: Int)
    }

}