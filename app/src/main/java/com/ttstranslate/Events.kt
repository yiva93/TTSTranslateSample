package com.ttstranslate

import com.ttstranslate.domain.model.Translation

sealed interface Event {
    class RecognitionResult(val result: String) : Event
    class FavoriteRemovedEvent(val favoriteId: Long) : Event
    class LoadTranslation(val translation: Translation) : Event
}