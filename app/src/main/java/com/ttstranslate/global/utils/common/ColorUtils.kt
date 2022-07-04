package com.ttstranslate.global.utils.common

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Get ColorInt representation of a themed attribute value
 * @param colorAttr Color attribute reference (R.attr.colorPrimary)
 */
@ColorInt
fun Context.getColorAttribute(@AttrRes colorAttr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(colorAttr, typedValue, true)
    return ContextCompat.getColor(this, typedValue.resourceId)
}

/**
 * Get ColorStateList from a themed attribute value
 * @param colorAttr Color attribute reference (R.attr.colorPrimary)
 */
fun Context.getTintAttribute(@AttrRes colorAttr: Int): ColorStateList {
    val typedValue = TypedValue()
    theme.resolveAttribute(colorAttr, typedValue, true)
    return ContextCompat.getColorStateList(this, typedValue.resourceId)!!
}

/**
 * Get ColorInt representation of a themed attribute value
 * @see Activity.getColorAttribute
 */
@ColorInt
fun Fragment.getColorAttribute(@AttrRes colorAttr: Int): Int =
    requireContext().getColorAttribute(colorAttr)

/**
 * Get ColorStateList from a themed attribute value
 * @see Activity.getTintAttribute
 */
fun Fragment.getTintAttribute(@AttrRes colorAttr: Int): ColorStateList =
    requireContext().getTintAttribute(colorAttr)

/**
 * Set backgroundTintList based on the color resource value
 * @param res Tint color resource
 */
fun View.setBackgroundTintRes(@ColorRes res: Int) {
    context?.also { backgroundTintList = ContextCompat.getColorStateList(it, res) }
}

@Deprecated(
    message = "Use setBackgroundTintRes",
    replaceWith = ReplaceWith("setBackgroundTintRes(res)")
)
fun View.setBackgroundTintList(@ColorRes res: Int) = setBackgroundTintRes(res)

/**
 * Set imageTintList based on the color resource value
 * @param res Tint color resource
 */
fun ImageView.setImageTintRes(@ColorRes res: Int) {
    context?.also { imageTintList = ContextCompat.getColorStateList(it, res) }
}

@Deprecated(
    message = "Use setImageTintRes",
    replaceWith = ReplaceWith("setImageTintRes(res)")
)
fun ImageView.setImageTintList(@ColorRes res: Int) = setImageTintRes(res)

/**
 * Set text color based on the color resource value
 * @param res Text color resource
 */
fun TextView.setTextColorRes(@ColorRes res: Int) {
    context?.also { setTextColor(ContextCompat.getColor(it, res)) }
}