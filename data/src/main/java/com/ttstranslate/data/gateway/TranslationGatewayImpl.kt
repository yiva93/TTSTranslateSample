package com.ttstranslate.data.gateway

import com.ttstranslate.data.network.LibreTranslateApi
import com.ttstranslate.data.network.request.TranslateSentenceRequest
import com.ttstranslate.data.storage.dao.FavoritesDao
import com.ttstranslate.domain.gateway.TranslationGateway
import com.ttstranslate.domain.model.Translation
import com.ttstranslate.domain.model.enums.Language

class TranslationGatewayImpl(
    private val libreTranslateApi: LibreTranslateApi,
    private val favoritesDao: FavoritesDao
    // private val preferences: Preferences
) : TranslationGateway {
    override suspend fun translateSentence(
        text: String,
        languageFrom: Language,
        languageTo: Language
    ): Translation {
        val result = libreTranslateApi.translate(
            TranslateSentenceRequest(
                query = text,
                source = languageFrom.tag,
                target = languageTo.tag,
                format = TRANSLATE_FORMAT
            )
        ).translatedText
        return Translation(
            textFrom = text,
            result = result,
            languageFromTo = Pair(languageFrom, languageTo),
            favoriteId = favoritesDao.getByFromField(text)?.id,
        )
    }

    companion object {
        private const val TRANSLATE_FORMAT = "text"
    }
}