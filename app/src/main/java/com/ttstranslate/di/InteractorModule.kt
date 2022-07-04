package com.ttstranslate.di

import com.ttstranslate.domain.interactor.favorite.AddFavoriteUseCase
import com.ttstranslate.domain.interactor.favorite.GetFavoriteTranslationPagedUseCase
import com.ttstranslate.domain.interactor.favorite.RemoveFavoriteUseCase
import com.ttstranslate.domain.interactor.translate.TranslateSentenceUseCase
import org.koin.dsl.module

internal val interactorModule = module {
    single { TranslateSentenceUseCase(get(), get()) }
    single { AddFavoriteUseCase(get(), get()) }
    single { RemoveFavoriteUseCase(get(), get()) }
    single { GetFavoriteTranslationPagedUseCase(get(), get()) }
}