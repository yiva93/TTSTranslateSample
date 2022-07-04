package com.ttstranslate.domain.interactor.translate

import com.ttstranslate.domain.gateway.TranslationGateway
import com.ttstranslate.domain.global.DispatcherProvider
import com.ttstranslate.domain.global.UseCaseWithParams
import com.ttstranslate.domain.model.Translation
import com.ttstranslate.domain.model.enums.Language

class TranslateSentenceUseCase(
    private val translationGateway: TranslationGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<TranslateSentenceUseCase.Params, Translation>(dispatcherProvider.io) {

    override suspend fun execute(parameters: Params): Translation {
        return translationGateway.translateSentence(
            text = parameters.text,
            languageFrom = parameters.languageFrom,
            languageTo = parameters.languageTo
        )
    }

    data class Params(
        val text: String,
        val languageFrom: Language,
        val languageTo: Language
    )
}