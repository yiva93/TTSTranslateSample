package com.ttstranslate.di

import com.ttstranslate.data.gateway.FavoriteGatewayImpl
import com.ttstranslate.data.gateway.TranslationGatewayImpl
import com.ttstranslate.domain.gateway.FavoriteGateway
import com.ttstranslate.domain.gateway.TranslationGateway
import org.koin.dsl.module

internal val gatewayModule = module {
    single<TranslationGateway> { TranslationGatewayImpl(get(), get()) }
    single<FavoriteGateway> { FavoriteGatewayImpl(get()) }
}