package com.ttstranslate.global.utils.common

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

fun Fragment?.hideKeyboard() = this?.activity.hideKeyboard()

fun Fragment?.hideKeyboard(view: View) = this?.activity.hideKeyboard(view)

fun Activity?.hideKeyboard() {
    val view = this?.currentFocus ?: return
    hideKeyboard(view)
}

fun Activity?.hideKeyboard(view: View) {
    val imm = this?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment?.toggleKeyboard(forced: Boolean = false) = this?.activity.toggleKeyboard(forced)

fun Activity?.toggleKeyboard(forced: Boolean = false) {
    val imm = this?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.toggleSoftInput(
        if (forced) InputMethodManager.SHOW_FORCED else InputMethodManager.SHOW_IMPLICIT,
        0
    )
}

fun Fragment?.showKeyboard(forced: Boolean = false) = this?.activity.showKeyboard(forced)

fun Activity?.showKeyboard(forced: Boolean = false) {
    hideKeyboard()
    toggleKeyboard(forced)
}

fun Fragment?.showKeyboard(view: View, forced: Boolean = false) =
    this?.activity.showKeyboard(view, forced)

fun Activity?.showKeyboard(view: View, forced: Boolean = false) {
    view.requestFocus()
    val imm = this?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.showSoftInput(
        view,
        if (forced) InputMethodManager.SHOW_FORCED else InputMethodManager.SHOW_IMPLICIT
    )
}