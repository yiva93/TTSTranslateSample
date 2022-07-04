package com.ttstranslate.domain.gateway

import androidx.paging.DataSource
import com.ttstranslate.domain.model.Translation

interface FavoriteGateway {
    suspend fun getPagingSource(): DataSource.Factory<Int, Translation>
    suspend fun addFavorite(translation: Translation): Long
    suspend fun removeFavorite(favoriteId: Long)
}