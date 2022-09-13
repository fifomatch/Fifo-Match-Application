package com.fifo.match.ui.activity.membership.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.fifo.match.R
import com.fifo.match.databinding.ItemPlanBinding
import com.fifo.match.model.PlanModel
import com.fifo.match.ui.base.BaseViewHolder
import com.fifo.match.utils.extensions.gone
import com.fifo.match.utils.extensions.roundBorderedViewFromResId
import com.fifo.match.utils.extensions.visible
import kotlinx.android.synthetic.main.item_plan.view.*

internal class PlanAdapter(
    private val listPlan: ArrayList<PlanModel>,

) : RecyclerView.Adapter<PlanAdapter.ViewHolder>() {

    private var selectedItemPosition: Int = 0

    internal inner class ViewHolder(binding: ItemPlanBinding) :
        BaseViewHolder<ItemPlanBinding>(binding) {
        fun bindView(data: PlanModel) {
            binding.apply {
                tvShow.roundBorderedViewFromResId(60, R.color.yellow, R.color.yellow, 1)

                if (selectedItemPosition == absoluteAdapterPosition) {
                    tvShow.visible()
                    llPlan.setBackgroundResource(R.drawable.shape_plan)
                } else {
                    tvShow.gone()
                    llPlan.setBackgroundResource(0)
                }

                llPlan.setOnClickListener {
                    selectedItemPosition = absoluteAdapterPosition
                    notifyDataSetChanged()
                    itemView.performClick()
                }
                itemView.setOnClickListener {
                    llPlan.performClick()
                }

                data.let {
                    val strParts = data.name!!.split(" ")
                    val strTwo = strParts[1]
                    tvPrise.text = "$strTwo"
                    tvPlanName.text = data.slug
                }


            }
        }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_plan,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 1) {
            holder.itemView.planImage.setBackgroundResource(R.drawable.ic_plan_logo)
        } else {
            holder.itemView.planImage.setBackgroundResource(R.drawable.ic_premium)
        }

        holder.bindView(listPlan[position])

    }

    override fun getItemCount(): Int {
        return listPlan.size
    }

}