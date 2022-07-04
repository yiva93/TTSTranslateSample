package com.ttstranslate.domain.interactor.favorite

import androidx.paging.DataSource
import com.ttstranslate.domain.gateway.FavoriteGateway
import com.ttstranslate.domain.global.DispatcherProvider
import com.ttstranslate.domain.global.UseCase
import com.ttstranslate.domain.model.Translation

class GetFavoriteTranslationPagedUseCase(
    private val favoriteGateway: FavoriteGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<DataSource.Factory<Int, Translation>>(dispatcherProvider.io) {

    override suspend fun execute(): DataSource.Factory<Int, Translation> {
        return favoriteGateway.getPagingSource()
    }
}