package com.ttstranslate.feature.recognition

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ttstranslate.Event
import com.ttstranslate.domain.model.enums.Language
import com.ttstranslate.global.dispatcher.event.EventDispatcher
import com.ttstranslate.global.utils.asLiveData

class VoiceRecognitionViewModel(private val eventDispatcher: EventDispatcher) : ViewModel() {
    private val _languageLiveData = MutableLiveData(Language.EN)
    val languageLiveData = _languageLiveData.asLiveData()

    fun updateLocale(language: Language) {
        _languageLiveData.value = language
    }

    fun onRecognitionEnded(result: String) {
        eventDispatcher.sendEvent(Event.RecognitionResult(result))
    }
}