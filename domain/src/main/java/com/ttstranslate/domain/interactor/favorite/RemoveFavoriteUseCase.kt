package com.ttstranslate.domain.interactor.favorite

import com.ttstranslate.domain.gateway.FavoriteGateway
import com.ttstranslate.domain.global.DispatcherProvider
import com.ttstranslate.domain.global.UseCaseWithParams

class RemoveFavoriteUseCase(
    private val favoriteGateway: FavoriteGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<Long, Unit>(dispatcherProvider.io) {

    override suspend fun execute(parameters: Long) {
        return favoriteGateway.removeFavorite(parameters)
    }
}