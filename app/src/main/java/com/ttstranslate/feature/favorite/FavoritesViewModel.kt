package com.ttstranslate.feature.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ttstranslate.Event
import com.ttstranslate.domain.interactor.favorite.GetFavoriteTranslationPagedUseCase
import com.ttstranslate.domain.interactor.favorite.RemoveFavoriteUseCase
import com.ttstranslate.domain.model.Translation
import com.ttstranslate.global.dispatcher.error.ErrorHandler
import com.ttstranslate.global.dispatcher.event.EventDispatcher
import com.ttstranslate.global.dispatcher.notifier.Notifier
import com.ttstranslate.global.utils.asLiveData
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val getFavoriteTranslationPagedUseCase: GetFavoriteTranslationPagedUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val errorHandler: ErrorHandler,
    private val notifier: Notifier,
    private val eventDispatcher: EventDispatcher
) : ViewModel() {
    lateinit var favoritesLiveData: LiveData<PagedList<Translation>>

    private val _pagingDataInitialized = MutableLiveData(false)
    val pagingDataInitialized = _pagingDataInitialized.asLiveData()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            getFavoriteTranslationPagedUseCase.invoke().process(
                ::onGetPagedListSuccess, ::onError
            )
        }
    }

    private fun onError(t: Throwable) {
        errorHandler.proceed(t) {
            notifier.sendMessage(it)
        }
    }

    private fun onGetPagedListSuccess(data: DataSource.Factory<Int, Translation>) {
        val pagedListConfig = PagedList.Config.Builder()
            .setPageSize(5)
            .setPrefetchDistance(1)
            .setEnablePlaceholders(false)
            .build()
        favoritesLiveData = LivePagedListBuilder(data, pagedListConfig).build()
        _pagingDataInitialized.value = true
    }

    fun onRemoveFavorite(favoriteId: Long?) {
        if (favoriteId==null)
            return
        viewModelScope.launch {
            removeFavoriteUseCase.invoke(favoriteId)
                .process({ onRemoveFavoriteSuccess(favoriteId) }, ::onError)

        }
    }

    private fun onRemoveFavoriteSuccess(favoriteId: Long) {
        eventDispatcher.sendEvent(Event.FavoriteRemovedEvent(favoriteId))
    }

    fun onFavoriteItemClick(translation: Translation) {
        eventDispatcher.sendEvent(Event.LoadTranslation(translation.copy()))
    }
}