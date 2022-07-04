package com.ttstranslate.observer

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class AppLifecycleObserver(private val context: Context) : LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            /**
             * When app is started
             */
            Lifecycle.Event.ON_CREATE -> {

            }

            /**
             * When app enters foreground
             */
            Lifecycle.Event.ON_START -> {
            }

            /**
             * When app enters background
             */
            Lifecycle.Event.ON_STOP -> {
            }

            /**
             * When app surely enters foreground
             */
            Lifecycle.Event.ON_RESUME -> {
            }

            else -> {
            }
        }
    }
}