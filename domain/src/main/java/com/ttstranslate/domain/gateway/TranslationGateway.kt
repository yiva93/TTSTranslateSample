package com.ttstranslate.domain.gateway

import com.ttstranslate.domain.model.Translation
import com.ttstranslate.domain.model.enums.Language

interface TranslationGateway {
    suspend fun translateSentence(text: String, languageFrom: Language, languageTo: Language): Translation
}