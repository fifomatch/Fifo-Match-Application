package com.fifo.match.ui.activity.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fifo.match.R
import com.fifo.match.databinding.ItemTopQualityBinding

import com.fifo.match.ui.base.BaseViewHolder

internal class BodyTypeFilterAdapter(private val listBody: ArrayList<String>) : RecyclerView.Adapter<BodyTypeFilterAdapter.NotificationViewHolder>() {

    private var selectedItems = ""

     inner class NotificationViewHolder(binding: ItemTopQualityBinding) :
        BaseViewHolder<ItemTopQualityBinding>(binding) {

            fun bind(listSeeking: String) {
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
        return NotificationViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_top_quality, parent, false))
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(listBody[position])
    }

    override fun getItemCount(): Int {
        return listBody.size
    }

    fun setSelectedData(listBody: String) {
        this.selectedItems = listBody
        notifyDataSetChanged()
    }
}