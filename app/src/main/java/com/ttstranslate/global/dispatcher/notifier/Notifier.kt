package com.ttstranslate.global.dispatcher.notifier

import androidx.annotation.StringRes
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class Notifier {
    private val _messageBus: MutableSharedFlow<SystemMessage> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val messageBus: SharedFlow<SystemMessage> = _messageBus

    fun sendMessage(text: String, level: SystemMessage.Level = SystemMessage.Level.NORMAL) {
        val msg = SystemMessage(
            text = text,
            type = SystemMessage.Type.BAR,
            level = level
        )
        _messageBus.tryEmit(msg)
    }

    fun sendMessage(
        @StringRes stringRes: Int,
        level: SystemMessage.Level = SystemMessage.Level.NORMAL
    ) {
        val msg = SystemMessage(
            textRes = stringRes,
            type = SystemMessage.Type.BAR,
            level = level
        )
        _messageBus.tryEmit(msg)
    }

    fun sendAlert(text: String) {
        val msg = SystemMessage(
            text = text,
            type = SystemMessage.Type.ALERT
        )
        _messageBus.tryEmit(msg)
    }

    fun sendAlert(@StringRes stringRes: Int) {
        val msg = SystemMessage(
            textRes = stringRes,
            type = SystemMessage.Type.ALERT
        )
        _messageBus.tryEmit(msg)
    }

    fun sendActionMessage(
        @StringRes textRes: Int,
        @StringRes actionTextRes: Int,
        actionCallback: () -> Unit?
    ) {
        val msg = SystemMessage(
            textRes = textRes,
            actionTextRes = actionTextRes,
            actionCallback = actionCallback,
            type = SystemMessage.Type.ACTION
        )
        _messageBus.tryEmit(msg)
    }

    fun sendActionMessage(text: String, actionText: String, actionCallback: () -> Unit?) {
        val msg = SystemMessage(
            text = text,
            actionText = actionText,
            actionCallback = actionCallback,
            type = SystemMessage.Type.ACTION
        )
        _messageBus.tryEmit(msg)
    }
}
