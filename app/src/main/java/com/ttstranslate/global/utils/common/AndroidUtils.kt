package com.ttstranslate.global.utils.common

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

@Suppress("NOTHING_TO_INLINE")
@ChecksSdkIntAtLeast(parameter = 0)
inline fun isApiAtLeast(apiCode: Int): Boolean = Build.VERSION.SDK_INT >= apiCode

@Suppress("NOTHING_TO_INLINE")
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
inline fun isAtLeastOreo(): Boolean = isApiAtLeast(Build.VERSION_CODES.O)