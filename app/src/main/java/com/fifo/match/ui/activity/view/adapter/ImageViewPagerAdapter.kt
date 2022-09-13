package com.fifo.match.ui.activity.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.fifo.match.R
import com.fifo.match.model.UserDetailsModel
import com.fifo.match.network.utils.Constants
import com.fifo.match.ui.activity.fullImage.ImageViewActivity
import java.util.*

internal class ImageViewPagerAdapter(var context: Context, private val listPhotos: ArrayList<UserDetailsModel.UserDetails.Photo>) : PagerAdapter() {

    var mLayoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        val size: Int = listPhotos.size
        return if (size > 3) 3 else size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.item_imagepager, container, false)
        val imageView = itemView.findViewById<View>(R.id.image) as ImageView
        val dataImage = listPhotos[position].name
        Glide.with(context).load(dataImage).into(imageView)
        imageView.setOnClickListener {
            val intent = Intent(itemView.context, ImageViewActivity::class.java)
            intent.putExtra(Constants.IMAGE_FLAG , dataImage)
            itemView.context.startActivity(intent)
        }

        Objects.requireNonNull(container).addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }

}