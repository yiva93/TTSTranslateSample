package com.ttstranslate.data.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ttstranslate.data.BuildConfig
import com.ttstranslate.data.storage.dao.FavoritesDao
import com.ttstranslate.data.storage.entity.FavoritesEntity

@Database(
    entities = [
        FavoritesEntity::class
    ],
    version = BuildConfig.SCHEMA_VERSION,
    exportSchema = false
)

abstract class TTSTranslateDb : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
}
