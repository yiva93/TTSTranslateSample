package com.ttstranslate.domain.model

import com.ttstranslate.domain.model.enums.Language

data class Translation(
    var favoriteId: Long? = null,
    val textFrom: String,
    val result: String,
    val languageFromTo: Pair<Language, Language>,
    var dateAdded: Long? = null
)