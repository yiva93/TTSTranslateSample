package com.ttstranslate.di

import com.ttstranslate.data.network.LibreTranslateApi
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

internal val apiModule = module {
    single { get<Retrofit>(named("LibreTranslateRetrofit")).create(LibreTranslateApi::class.java) }
}