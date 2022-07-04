package com.ttstranslate.feature.main

import androidx.annotation.IdRes
import com.ttstranslate.R

enum class BottomNavigationTab(@IdRes val id: Int) {
    TRANSLATION(R.id.navigationTranslation),
    FAVORITES(R.id.navigationFavorites);

    companion object {
        operator fun get(@IdRes id: Int?) = values().find { it.id == id } ?: TRANSLATION
    }
}