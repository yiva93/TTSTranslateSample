package com.ttstranslate.domain.model.enums

import android.os.Parcelable
import com.ttstranslate.domain.R
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
enum class Language(
    val id: Int,
    val tag: String,
    val nameRes: Int,
    val locale: Locale
) : Parcelable {
    RU(1, "ru", R.string.language_russian, Locale("ru", "RU")),
    EN(2, "en", R.string.language_english, Locale.US);

    companion object {
        operator fun get(tag: String) = values().find { it.tag == tag } ?: RU
        operator fun get(id: Int) = values().find { it.id == id } ?: RU
    }
}