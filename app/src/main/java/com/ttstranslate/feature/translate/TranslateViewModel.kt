package com.ttstranslate.feature.translate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ttstranslate.Event
import com.ttstranslate.domain.interactor.favorite.AddFavoriteUseCase
import com.ttstranslate.domain.interactor.favorite.RemoveFavoriteUseCase
import com.ttstranslate.domain.interactor.translate.TranslateSentenceUseCase
import com.ttstranslate.domain.model.Translation
import com.ttstranslate.domain.model.enums.Language
import com.ttstranslate.global.dispatcher.error.ErrorHandler
import com.ttstranslate.global.dispatcher.event.EventDispatcher
import com.ttstranslate.global.dispatcher.notifier.Notifier
import com.ttstranslate.global.utils.asLiveData
import com.ttstranslate.global.utils.common.swap
import com.ttstranslate.global.viewmodel.LoaderViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class TranslateViewModel(
    private val translateSentenceUseCase: TranslateSentenceUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val errorHandler: ErrorHandler,
    private val notifier: Notifier,
    private val eventDispatcher: EventDispatcher
) : LoaderViewModel(), EventDispatcher.EventListener {
    private val _languageLiveData = MutableLiveData(Pair(Language.RU, Language.EN))
    val languageLiveData = _languageLiveData.asLiveData()

    private val _translationLiveData = MutableLiveData<Translation?>()
    val translationLiveData = _translationLiveData.asLiveData()

    private val _recognitionLiveData = MutableLiveData<String>()
    val recognitionLiveData = _recognitionLiveData.asLiveData()

    private val searchQueryObservable = MutableStateFlow<String?>(null)
    var searchQuery: String
        get() = searchQueryObservable.value.orEmpty()
        set(value) {
            searchQueryObservable.value = value
        }

    init {
        eventDispatcher.addEventListener(Event.RecognitionResult::class, this)
        eventDispatcher.addEventListener(Event.FavoriteRemovedEvent::class, this)
        eventDispatcher.addEventListener(Event.LoadTranslation::class, this)
        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQueryObservable
                .debounce(QUERY_DELAY_MS)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (!query.isNullOrBlank() && _languageLiveData.value != null) {
                        showProgress()
                        translateSentenceUseCase
                            .invoke(
                                TranslateSentenceUseCase.Params(
                                    query,
                                    _languageLiveData.value!!.first,
                                    _languageLiveData.value!!.second
                                )
                            )
                            .process(::onTranslateSuccess, ::onError)
                    } else onTranslateSuccess(null)
                }
        }
    }

    private fun onTranslateSuccess(result: Translation?) {
        hideProgress()
        _translationLiveData.value = result
    }


    fun updateSearchQuery(query: String) {
        searchQuery = query.trim()
    }

    fun onFavoriteClick() {
        val favoriteId = _translationLiveData.value?.favoriteId
        if (favoriteId != null)
            removeFavorite()
        else addFavorite()
    }

    private fun addFavorite() {
        _translationLiveData.value?.let { transition ->
            viewModelScope.launch {
                addFavoriteUseCase.invoke(
                    transition.apply {
                        dateAdded = System.currentTimeMillis()
                    }
                )
                    .process(::onAddFavoriteSuccess, ::onError)

            }
        }
    }

    private fun onAddFavoriteSuccess(favoriteId: Long) {
        _translationLiveData.value = _translationLiveData.value?.also { it.favoriteId = favoriteId }
    }

    private fun removeFavorite() {
        _translationLiveData.value?.favoriteId?.let { favoriteId ->
            viewModelScope.launch {
                removeFavoriteUseCase.invoke(favoriteId)
                    .process({ onRemoveFavoriteSuccess() }, ::onError)

            }
        }
    }

    private fun onRemoveFavoriteSuccess() {
        _translationLiveData.value = _translationLiveData.value?.also { it.favoriteId = null }
    }

    fun toggleTranslateLanguages() {
        _languageLiveData.value = _languageLiveData.value?.let { it.first swap it.second }
    }

    private fun onError(t: Throwable) {
        errorHandler.proceed(t) {
            hideProgress()
            notifier.sendMessage(it)
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.RecognitionResult -> _recognitionLiveData.value = event.result
            is Event.FavoriteRemovedEvent -> {
                _translationLiveData.value?.let { translation ->
                    if (translation.favoriteId != null && translation.favoriteId == event.favoriteId)
                        _translationLiveData.value =
                            _translationLiveData.value?.also { it.favoriteId = null }
                }
            }
            is Event.LoadTranslation -> _translationLiveData.value = event.translation
        }
    }

    override fun onCleared() {
        eventDispatcher.removeEventListener(this)
        super.onCleared()
    }

    companion object {
        private const val QUERY_DELAY_MS = 1000L
    }
}