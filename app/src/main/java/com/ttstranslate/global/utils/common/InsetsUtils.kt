package com.ttstranslate.global.utils.common

import android.view.View
import android.view.ViewGroup
import androidx.core.view.*

fun View.addSystemWindowInsetToPadding(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false,
    leftOffset: Int = 0,
    topOffset: Int = 0,
    rightOffset: Int = 0,
    bottomOffset: Int = 0
) {
    val (initialLeft, initialTop, initialRight, initialBottom) = listOf(
        paddingLeft + leftOffset,
        paddingTop + topOffset,
        paddingRight + rightOffset,
        paddingBottom + bottomOffset
    )

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemInsets =
            insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())

        view.updatePadding(
            left = initialLeft + (if (left) systemInsets.left.coerceAtLeast(0) else 0),
            top = initialTop + (if (top) systemInsets.top.coerceAtLeast(0) else 0),
            right = initialRight + (if (right) systemInsets.right.coerceAtLeast(0) else 0),
            bottom = initialBottom + (if (bottom) systemInsets.bottom.coerceAtLeast(0) else 0)
        )

        insets
    }
}

fun View.addSystemWindowInsetToMargin(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false,
    leftOffset: Int = 0,
    topOffset: Int = 0,
    rightOffset: Int = 0,
    bottomOffset: Int = 0
) {
    val (initialLeft, initialTop, initialRight, initialBottom) = listOf(
        marginLeft + leftOffset,
        marginTop + topOffset,
        marginRight + rightOffset,
        marginBottom + bottomOffset
    )

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemInsets =
            insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())

        view.updateLayoutParams {
            (this as? ViewGroup.MarginLayoutParams)?.let {
                updateMargins(
                    left = initialLeft + (if (left) systemInsets.left.coerceAtLeast(0) else 0),
                    top = initialTop + (if (top) systemInsets.top.coerceAtLeast(0) else 0),
                    right = initialRight + (if (right) systemInsets.right.coerceAtLeast(0) else 0),
                    bottom = initialBottom + (if (bottom) systemInsets.bottom.coerceAtLeast(0) else 0)
                )
            }
        }

        insets
    }
}