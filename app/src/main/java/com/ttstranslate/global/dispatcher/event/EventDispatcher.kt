package com.ttstranslate.global.dispatcher.event

import android.os.Handler
import android.os.Looper
import com.ttstranslate.Event
import java.util.*
import kotlin.reflect.KClass

class EventDispatcher {
    private val eventListeners = hashMapOf<String, MutableList<EventListener>>()

    fun addEventListener(
        customEventClass: KClass<out Event>,
        listener: EventListener
    ): EventListener {
        val key = customEventClass.java.name
        if (eventListeners[key] == null) {
            eventListeners[key] = mutableListOf()
        }

        eventListeners[key]?.add(listener)

        return listener
    }

    fun removeEventListener(listener: EventListener) {
        eventListeners.values
            .filter { it.any() }
            .forEach { it.remove(listener) }
    }

    fun sendEvent(customEvent: Event) {
        val key = customEvent::class.java.name
        eventListeners
            .filter { it.key == key }
            .flatMap { it.value }
            .forEach { listener ->
                Handler(Looper.getMainLooper()).post {
                    listener.onEvent(customEvent)
                }
            }
    }

    fun interface EventListener {
        fun onEvent(event: Event)
    }
}