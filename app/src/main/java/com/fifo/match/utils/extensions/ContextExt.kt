package com.fifo.match.utils.extensions

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat


fun Context.getColorCompat(@ColorRes resourceId: Int) = ContextCompat.getColor(this, resourceId).toString()

fun Context.getDrawablexx(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)



fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

 fun setTextViewDrawableColor(textView: TextView, color: Int) {
    for (drawable in textView.compoundDrawables) {
        if (drawable != null) {
            drawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(textView.context, color), PorterDuff.Mode.SRC_IN)
        }
    }
}


fun Context.share(text: String, subject: String = ""): Boolean {
    val intent = Intent()
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    intent.putExtra(Intent.EXTRA_TEXT, text)
    try {
        startActivity(Intent.createChooser(intent, null))
        return true
    } catch (e: ActivityNotFoundException) {
        return false
    }
}
