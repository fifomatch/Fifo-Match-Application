package com.fifo.match.utils

import android.graphics.drawable.GradientDrawable

class CardView(pStartColor: Int, radis: FloatArray?) :
    GradientDrawable(Orientation.BOTTOM_TOP, intArrayOf(pStartColor, pStartColor, pStartColor)) {
    init {
        shape = RECTANGLE
        cornerRadii = radis
    }
}
