package com.ttstranslate.feature.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.ttstranslate.Event
import com.ttstranslate.global.dispatcher.event.EventDispatcher

class BottomNavigationViewModel(
    private val eventDispatcher: EventDispatcher
) : ViewModel(),
    EventDispatcher.EventListener {
    private val _navigationTabLiveData = MutableLiveData<BottomNavigationTab>()
    val navigationTabLiveData = _navigationTabLiveData
        .distinctUntilChanged()

    fun updateTab(tab: BottomNavigationTab) {
        _navigationTabLiveData.value = tab
    }

    init {
        eventDispatcher.addEventListener(Event.LoadTranslation::class, this)
    }

    override fun onCleared() {
        super.onCleared()
        eventDispatcher.removeEventListener(this)
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.LoadTranslation -> updateTab(BottomNavigationTab.TRANSLATION)
            else -> Unit
        }
    }
}