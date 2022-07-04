package com.ttstranslate.global.ui.view

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.core.util.ObjectsCompat

/**
 * A FrameLayout which memorizes the window insets and propagates them to child views before they are measured.
 * You can use this layout as a fragment container in place of a standard FrameLayout to
 * propagate window insets to attached fragments.
 *
 * @author Christophe Beyls
 */
class WindowInsetsFrameLayout : FrameLayout {
    private var mLastInsets: Any? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        if (!ObjectsCompat.equals(mLastInsets, insets)) {
            mLastInsets = insets
            requestLayout()
        }
        return insets.consumeSystemWindowInsets()
    }

    override fun fitSystemWindows(insets: Rect): Boolean {
        if (!ObjectsCompat.equals(mLastInsets, insets)) {
            if (mLastInsets == null) {
                mLastInsets = Rect(insets)
            } else {
                (mLastInsets as Rect).set(insets)
            }
            requestLayout()
        }
        return true
    }

    @Suppress("DEPRECATION")
    @SuppressLint("DrawAllocation", "ObsoleteSdkInt")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mLastInsets != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                val wi = mLastInsets as WindowInsets?
                val childCount = childCount
                for (i in 0 until childCount) {
                    val child = getChildAt(i)
                    if (child.visibility != View.GONE) {
                        child.dispatchApplyWindowInsets(wi)
                    }
                }
            } else {
                super.fitSystemWindows(Rect(mLastInsets as Rect?))
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}