package com.fifo.match.utils.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.format.DateFormat
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.fifo.match.R
import com.fifo.match.network.utils.Utility
import es.dmoral.toasty.Toasty
import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun Context.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, message, duration).show()

fun Context.toastError(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) = Toasty.error(this, message, duration,false).show()
//
fun Context.toastSuccess(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) = Toasty.success(this, message, duration,true).show()
//
fun Context.toastInfo(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) = Toasty.info(this, message, duration,true).show()

inline fun Activity.alertDialog(body: AlertDialog.Builder.() -> AlertDialog.Builder): AlertDialog {
    return AlertDialog.Builder(this).body().show()
}

fun Activity.hideKeyboard(view: View) {
    hideKeyboard(currentFocus ?: View(this))
}

inline fun <reified T : Activity> Context.openActivity(vararg params: Pair<String, Any>) {
    val intent = Intent(this, T::class.java)
    intent.putExtras(*params)
    this.startActivity(intent)
}

fun Activity.setStatusBarTransparent(color: String, isDark: Boolean) {
//    this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (isDark) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = Color.parseColor(color)
    } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.parseColor(color)
    }
}

fun Activity.transparentStatusBarIsBoolean(isDark: Boolean) {
    this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
    this.window.statusBarColor = Color.TRANSPARENT
    if (isDark) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

fun Activity.transparentStatusBar() {
    this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
    this.window.statusBarColor = Color.TRANSPARENT
}

private fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
    val win = activity.window
    val winParams = win.attributes
    if (on) {
        winParams.flags = winParams.flags or bits
    } else {
        winParams.flags = winParams.flags and bits.inv()
    }
    win.attributes = winParams
}

fun Intent.putExtras(vararg params: Pair<String, Any>): Intent {
    if (params.isEmpty()) return this
    params.forEach { (key, value) ->
        when (value) {
            is Int -> putExtra(key, value)
            is Byte -> putExtra(key, value)
            is Char -> putExtra(key, value)
            is Long -> putExtra(key, value)
            is Float -> putExtra(key, value)
            is Short -> putExtra(key, value)
            is Double -> putExtra(key, value)
            is Boolean -> putExtra(key, value)
            is Bundle -> putExtra(key, value)
            is String -> putExtra(key, value)
            is IntArray -> putExtra(key, value)
            is ByteArray -> putExtra(key, value)
            is CharArray -> putExtra(key, value)
            is LongArray -> putExtra(key, value)
            is FloatArray -> putExtra(key, value)
            is Parcelable -> putExtra(key, value)
            is ShortArray -> putExtra(key, value)
            is DoubleArray -> putExtra(key, value)
            is BooleanArray -> putExtra(key, value)
            is CharSequence -> putExtra(key, value)
            is Array<*> -> {
                when {
                    value.isArrayOf<String>() ->
                        putExtra(key, value as Array<String?>)
                    value.isArrayOf<Parcelable>() ->
                        putExtra(key, value as Array<Parcelable?>)
                    value.isArrayOf<CharSequence>() ->
                        putExtra(key, value as Array<CharSequence?>)
                    else -> putExtra(key, value)
                }
            }
            is Serializable -> putExtra(key, value)
        }
    }
    return this
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}

fun Fragment.addFragment(fragment: Fragment, frameId: Int, backStackTag: String? = null) {
    childFragmentManager.inTransaction {
        add(frameId, fragment)
        backStackTag?.let { addToBackStack(fragment.javaClass.name) }
    }
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int, backStackTag: String? = null) {
    supportFragmentManager.inTransaction {
        replace(frameId, fragment)
        backStackTag?.let { addToBackStack(fragment.javaClass.name) }
    }
}


fun Fragment.replaceFragment(fragment: Fragment, frameId: Int, backStackTag: String? = null) {
    childFragmentManager.inTransaction {
        replace(frameId, fragment)
        backStackTag?.let { addToBackStack(fragment.javaClass.name) }
    }
}

fun View.roundBorderedViewFromResId(cornerRadius: Int, backgroundColor : Int, borderColor: Int, borderWidth : Int) {
    addOnLayoutChangeListener(object: View.OnLayoutChangeListener {
        override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
            val shape = GradientDrawable()
            shape.cornerRadius = cornerRadius.toFloat()//measuredHeight / 2f
            shape.setColor(ContextCompat.getColor(context,backgroundColor))
            shape.setStroke(borderWidth,ContextCompat.getColor(context,borderColor))
            background = shape
            removeOnLayoutChangeListener(this)
        }
    })
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}


fun getCurrentDate():String{
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    return sdf.format(Date())
}

fun parseDateToddMMyyyy(time: String?): String? {
    val inputPattern = "yyyy-MM-dd"
    val outputPattern = "dd MMM, yyyy"
    val inputFormat = SimpleDateFormat(inputPattern)
    val outputFormat = SimpleDateFormat(outputPattern)
    var date: Date? = null
    var str: String? = null
    try {
        date = inputFormat.parse(time)
        str = outputFormat.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return str
}

fun getDateTime(context: Context,timestamp: Long): String? {
    return try {
//        val sdf1 = SimpleDateFormat("dd MMM yyyy")
        val sdf1 = SimpleDateFormat("yyyy-mm-dd")
        var str1: String? = ""
        val netDate = Date(timestamp)
        val c1 = Calendar.getInstance() // today
        c1.add(Calendar.DAY_OF_YEAR, -1) // yesterday
        val c2 = Calendar.getInstance()
        c2.time = netDate // your date
        str1 = if (sdf1.format(netDate) == sdf1.format(Date().time)) {
            timeValue(context,timestamp.toString())

        } else if (c1[Calendar.YEAR] == c2[Calendar.YEAR]
            && c1[Calendar.DAY_OF_YEAR] == c2[Calendar.DAY_OF_YEAR]
        ) {
            context.getString(R.string.yesterday)
        } else {
            sdf1.format(netDate)
        }
        str1
    } catch (ex: Exception) {
        "xx"
    }
}
 fun timeValue(context: Context, time: String): String {
    try {
        var timeNew: String = Utility.getFormattedDateNotification(context, time)!!
        if (timeNew.equals("0m", ignoreCase = true)) {
            timeNew = "Now"
        } else if (timeNew.equals("0 min", ignoreCase = true)) {
            timeNew = "Now"
        } else if (timeNew.equals("+0 min", ignoreCase = true)) {
            timeNew = "Now"
        } else if (timeNew.equals("In 0 minutes", ignoreCase = true)) {
            timeNew = "Now"
        }
        return timeNew
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}