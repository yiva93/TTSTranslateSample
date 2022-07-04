package com.ttstranslate.global.utils

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.viewbinding.ViewBinding

typealias BindingProvider<B> = (LayoutInflater, ViewGroup?, Boolean) -> B

val <T : ViewBinding> T.context: Context
    get() = root.context

val <T : ViewBinding> T.resources: Resources
    get() = root.resources

fun <T : ViewBinding> T.getString(@StringRes res: Int) = context.getString(res)