package com.ttstranslate.di

import androidx.room.Room
import com.ttstranslate.data.storage.TTSTranslateDb
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val databaseModule = module {
    single {
        Room
            .databaseBuilder(
                androidContext(),
                TTSTranslateDb::class.java,
                "ttstranslate.db"
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    factory { get<TTSTranslateDb>().favoritesDao() }
}
