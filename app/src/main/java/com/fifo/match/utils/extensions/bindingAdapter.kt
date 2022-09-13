package com.fifo.match.utils.extensions

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

object BindingAdapter {

    @JvmStatic
    @BindingAdapter("onNavigationItemSelected")
    fun setOnNavigationItemSelectedListener(view: BottomNavigationView, listener: BottomNavigationView.OnNavigationItemSelectedListener?) {
        view.setOnNavigationItemSelectedListener(listener)
    }


    @JvmStatic
    @BindingAdapter("imageDrawable")
    fun ImageView.setImage(imageDrawable:Drawable) {
        this.setImageDrawable(imageDrawable)
    }

    @JvmStatic
    @BindingAdapter("imageSrc")
    fun setPaddingLeft(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource);
    }


}