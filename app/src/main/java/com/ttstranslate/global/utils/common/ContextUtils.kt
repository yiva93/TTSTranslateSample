package com.ttstranslate.global.utils.common

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import androidx.core.content.ContextCompat
import java.util.*

fun Context.convertDpToPx(value: Float): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    value,
    resources.displayMetrics
)

fun Context.convertPxToDp(value: Float): Float =
    value * (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)

fun Context.convertMmToPx(value: Float): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_MM,
    value,
    resources.displayMetrics
)

fun Context.convertPxToMm(value: Float): Float =
    value / (resources.displayMetrics.xdpi * (1.0f / 25.4f))

fun Context.getDeviceWidth(): Int = getDeviceSize().x

fun Context.getDeviceHeight(): Int = getDeviceSize().y

fun Context.getDeviceSize(): Point {
    val display = if (isApiAtLeast(Build.VERSION_CODES.R))
        this.display
    else (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val size = Point()
    display?.getSize(size)
    return size
}

fun Context.getColorCompat(color: Int) = ContextCompat.getColor(this, color)

fun Context.getDrawableCompat(drawable: Int) = ContextCompat.getDrawable(this, drawable)

fun Context.appLocale(): Locale = if (isApiAtLeast(Build.VERSION_CODES.N))
    resources.configuration.locales[0]
else resources.configuration.locale
