package com.ttstranslate.global.utils.common

import android.view.View

fun View?.setVisible() {
    this?.visibility = View.VISIBLE
}

fun View?.setInvisible() {
    this?.visibility = View.INVISIBLE
}

fun View?.setGone() {
    this?.visibility = View.GONE
}

inline fun <reified L : View> applyAll(vararg views: L, toDo: L.() -> Unit) =
    views.forEach { it.toDo() }