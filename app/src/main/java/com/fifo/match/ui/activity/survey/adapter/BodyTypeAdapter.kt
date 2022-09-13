package com.fifo.match.ui.activity.survey.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fifo.match.R
import com.fifo.match.databinding.ItemBodyTypeBinding

import com.fifo.match.ui.base.BaseViewHolder

internal class BodyTypeAdapter(private var listBody: ArrayList<String>) : RecyclerView.Adapter<BodyTypeAdapter.NotificationViewHolder>() {

//    private var selectedItemPosition: Int = -1
    private var selectedItems = ""

    internal inner class NotificationViewHolder(binding: ItemBodyTypeBinding) :
        BaseViewHolder<ItemBodyTypeBinding>(binding) {
            fun bind(data: String, position: Int) {
                binding.apply {
                    tvName.text = data
                    checkbox.isChecked = selectedItems == data

                    checkbox.setOnClickListener {
                        itemView.performClick()
                         selectedItems = data
                        notifyDataSetChanged();
                    }

                    itemView.setOnClickListener {
                        checkbox.performClick()
                    }

                }
            }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_body_type, parent, false))
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(listBody[position],position)

    }

    override fun getItemCount(): Int {
        return listBody.size
    }


    fun setSelectedData(listBody: String) {
        this.selectedItems = listBody
        notifyDataSetChanged()
    }

}