package com.ttstranslate.data.gateway

import androidx.paging.DataSource
import com.ttstranslate.data.storage.dao.FavoritesDao
import com.ttstranslate.data.storage.entity.toFavoriteEntity
import com.ttstranslate.data.storage.entity.toTranslation
import com.ttstranslate.domain.gateway.FavoriteGateway
import com.ttstranslate.domain.model.Translation

class FavoriteGatewayImpl(
    private val favoritesDao: FavoritesDao
    // private val preferences: Preferences
) : FavoriteGateway {
    override suspend fun getPagingSource(): DataSource.Factory<Int, Translation> {
        return favoritesDao.pagingSource().map { it.toTranslation() }
    }

    override suspend fun addFavorite(translation: Translation): Long {
        val favoriteEntity = favoritesDao.getByFromField(translation.textFrom)
        return favoriteEntity?.id ?: favoritesDao.add(translation.toFavoriteEntity())
    }

    override suspend fun removeFavorite(favoriteId: Long) {
        favoritesDao.deleteById(favoriteId)
    }
}