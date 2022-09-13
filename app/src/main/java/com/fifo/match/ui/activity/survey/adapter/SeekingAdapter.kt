package com.fifo.match.ui.activity.survey.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fifo.match.R
import com.fifo.match.databinding.ItemSeekingBinding

import com.fifo.match.ui.base.BaseViewHolder

internal class SeekingAdapter(private val listSeeking: ArrayList<String>) : RecyclerView.Adapter<SeekingAdapter.NotificationViewHolder>() {

    private var selectedItems = ""

    internal inner class NotificationViewHolder(binding: ItemSeekingBinding) :
        BaseViewHolder<ItemSeekingBinding>(binding) {
            fun bind(listSeeking: String, position: Int) {
                binding.apply {
                    tvTitle.text = listSeeking
                    if (selectedItems == listSeeking){
                        tvTitle.setBackgroundResource(R.drawable.shape_select)
                        tvTitle.setTextColor(AppCompatResources.getColorStateList(itemView.context, R.color.orange))
                    }else{
                        tvTitle.setBackgroundResource(R.drawable.shape_unselect)
                        tvTitle.setTextColor(AppCompatResources.getColorStateList(itemView.context, R.color.gray))
                    }
                    tvTitle.setOnClickListener {
                        itemView.performClick()
                        selectedItems = listSeeking
                        notifyDataSetChanged()

                    }
                    itemView.setOnClickListener {
                        tvTitle.performClick()
                    }
                }
            }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_seeking, parent, false))
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(listSeeking[position],position)
    }

    override fun getItemCount(): Int {
        return listSeeking.size
    }

    fun setSelectedData(listBody: String) {
        this.selectedItems = listBody
        notifyDataSetChanged()
    }

}