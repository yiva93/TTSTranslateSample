package com.ttstranslate.global.dispatcher.notifier

import androidx.annotation.StringRes

data class SystemMessage(
    @StringRes val textRes: Int? = null,
    val text: String? = null,
    @StringRes val actionTextRes: Int? = null,
    val actionText: String? = null,
    val actionCallback: (() -> Unit?)? = null,
    val type: Type,
    val level: Level = Level.NORMAL
) {
    enum class Type { ALERT, BAR, ACTION }

    enum class Level { NORMAL, ERROR }
}