package com.fifo.match.ui.activity.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fifo.match.R
import com.fifo.match.databinding.ItemTopQualityBinding
import com.fifo.match.ui.base.BaseViewHolder

internal class AppreciateQualitiesAdapter(private var list : List<String>) : RecyclerView.Adapter<AppreciateQualitiesAdapter.NotificationViewHolder>() {

    internal inner class NotificationViewHolder(binding: ItemTopQualityBinding) :
        BaseViewHolder<ItemTopQualityBinding>(binding) {
            fun bind(data: String) {
                binding.apply {
                    data.let {
                        tvTitle.text =data
                        tvTitle.setBackgroundResource(R.drawable.shape_select)
                        tvTitle.setTextColor(AppCompatResources.getColorStateList(itemView.context, R.color.orange))
                    }

                }
            }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_top_quality, parent, false))
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}