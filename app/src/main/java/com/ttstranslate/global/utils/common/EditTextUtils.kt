package com.ttstranslate.global.utils.common

import android.graphics.drawable.Drawable
import android.os.Build
import android.text.style.UnderlineSpan
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import java.lang.reflect.Field

/**
 * Set text and trigger input callbacks only if the text differs from the supplied value
 * @param value Value to check for equality
 */
fun EditText.syncToEnd(value: String?) {
    val notNullValue = value.orEmpty()
    if (notNullValue != text.toString()) {
        setText(notNullValue)
        setSelection(length())
    }
}

fun EditText.syncWithValue(value: String?) {
    val notNullValue = value.orEmpty()
    if (notNullValue != text.toString()) {
        setText(notNullValue)
    }
}

/**
 * Set text and trigger input callbacks only if the text differs from the supplied value.
 * Clear input on null values
 * @param value Value to check for equality
 */
fun EditText.syncWithValue(value: Double?) = value?.let {
    val editTextValue = text.toString().toDoubleOrNull() ?: 0.0
    if (editTextValue != it) {
        setText(value.toString())
    }
} ?: run {
    if (!text.isNullOrBlank()) {
        text = null
    }
}

/**
 * Set text and trigger input callbacks only if the text differs from the supplied value.
 * Clear input on null values
 * @param value Value to check for equality
 */
fun EditText.syncWithValue(value: Int?) = value?.let {
    val editTextValue = text.toString().toIntOrNull() ?: 0
    if (editTextValue != it) {
        setText(value.toString())
    }
} ?: run {
    if (!text.isNullOrBlank()) {
        text = null
    }
}

fun EditText?.removeFocusAndSpan() {
    this?.apply {
        clearFocus()
        postDelayed(
            { text.getSpans(0, text.length, UnderlineSpan::class.java).forEach(text::removeSpan) },
            100
        )
    }
}

fun EditText?.setAccentColor(@ColorInt accentColor: Int, @ColorInt selectionColor: Int? = null) {
    this?.apply {
        highlightColor = selectionColor ?: accentColor

        fun Drawable.copy() = constantState?.newDrawable()?.mutate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            textSelectHandle?.copy()?.let {
                it.setTint(accentColor)
                setTextSelectHandle(it)
            }
            textSelectHandleLeft?.copy()?.let {
                it.setTint(accentColor)
                setTextSelectHandleLeft(it)
            }
            textSelectHandleRight?.copy()?.let {
                it.setTint(accentColor)
                setTextSelectHandleRight(it)
            }
            textCursorDrawable?.copy()?.let {
                it.setTint(accentColor)
                textCursorDrawable = it
            }

        } else try {
            val fEditor: Field = TextView::class.java.getDeclaredField("mEditor")
            fEditor.isAccessible = true
            val editor = fEditor.get(this)
            val fSelectHandleLeft: Field =
                editor.javaClass.getDeclaredField("mSelectHandleLeft")
            val fSelectHandleLeftRes: Field =
                TextView::class.java.getDeclaredField("mTextSelectHandleLeftRes")
            val fSelectHandleRight: Field =
                editor.javaClass.getDeclaredField("mSelectHandleRight")
            val fSelectHandleRightRes: Field =
                TextView::class.java.getDeclaredField("mTextSelectHandleRightRes")
            val fSelectHandleCenter: Field =
                editor.javaClass.getDeclaredField("mSelectHandleCenter")
            val fSelectHandleCenterRes: Field =
                TextView::class.java.getDeclaredField("mTextSelectHandleRes")
            val fCursorDrawableRes: Field =
                TextView::class.java.getDeclaredField("mCursorDrawableRes")

            fun tintFieldDrawableRes(
                fieldFrom: Field,
                objectFrom: Any?,
                fieldTo: Field,
                objectTo: Any?,
                color: Int
            ) {
                fieldFrom.isAccessible = true
                fieldTo.isAccessible = true
                (fieldFrom.get(objectFrom) as? Int?)?.let {
                    val drawable = ContextCompat.getDrawable(context, it)?.copy()
                    drawable?.setTint(color)
                    fieldTo.set(objectTo, drawable)
                }
            }

            tintFieldDrawableRes(
                fSelectHandleCenterRes,
                this,
                fSelectHandleCenter,
                editor,
                accentColor
            )
            tintFieldDrawableRes(fSelectHandleLeftRes, this, fSelectHandleLeft, editor, accentColor)
            tintFieldDrawableRes(
                fSelectHandleRightRes,
                this,
                fSelectHandleRight,
                editor,
                accentColor
            )
            tintFieldDrawableRes(fCursorDrawableRes, this, fCursorDrawableRes, this, accentColor)
        } catch (ignored: Exception) {
        }
    }
}