package com.fifo.match.ui.activity.survey.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fifo.match.R
import com.fifo.match.databinding.ListQuaBinding

import com.fifo.match.ui.base.BaseViewHolder
import com.fifo.match.utils.extensions.toastInfo

//six fragment
internal class QualitiesTypeAdapter(private val list: ArrayList<String>) : RecyclerView.Adapter<QualitiesTypeAdapter.NotificationViewHolder>() {

    private var selectionPositions= hashSetOf<String>()

     inner class NotificationViewHolder(binding: ListQuaBinding) :
        BaseViewHolder<ListQuaBinding>(binding) {
        fun bind(list: String, position: Int) {
            binding.apply {
                tvTitle.text = list

                if (selectionPositions.contains(list.trim())){
                    tvTitle.setBackgroundResource(R.drawable.shape_select)
                    tvTitle.setTextColor(AppCompatResources.getColorStateList(itemView.context, R.color.orange))
                }else{
                    tvTitle.setBackgroundResource(R.drawable.shape_unselect)
                    tvTitle.setTextColor(AppCompatResources.getColorStateList(itemView.context, R.color.gray))
                }

                tvTitle.setOnClickListener {
                    Log.e("TGGGG", list)
                    if (selectionPositions.contains(list)) {
                        selectionPositions.remove(list)
                    } else {
                        Log.d("TAGxxx", selectionPositions.size.toString())
                        if (selectionPositions.size <= 3) {
                            selectionPositions.add(list)
                        } else{
                            itemView.context.toastInfo("Please select only 4 qualities")
                        }
                    }
                    notifyItemChanged(position)
                }
            }
        }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_qua, parent, false))
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(list[position],position)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getSelectedValues() = selectionPositions

    fun updateSelectedValues(selectedPos : HashSet<String>) {
        this.selectionPositions = selectedPos
        notifyDataSetChanged()
    }

}