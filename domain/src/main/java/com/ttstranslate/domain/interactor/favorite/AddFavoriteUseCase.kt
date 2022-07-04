package com.ttstranslate.domain.interactor.favorite

import com.ttstranslate.domain.gateway.FavoriteGateway
import com.ttstranslate.domain.global.DispatcherProvider
import com.ttstranslate.domain.global.UseCaseWithParams
import com.ttstranslate.domain.model.Translation

class AddFavoriteUseCase(
    private val favoriteGateway: FavoriteGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<Translation, Long>(dispatcherProvider.io) {

    override suspend fun execute(parameters: Translation): Long {
        return favoriteGateway.addFavorite(parameters)
    }
}