package com.ttstranslate.di

import com.ttstranslate.global.dispatcher.error.ErrorHandler
import com.ttstranslate.global.dispatcher.event.EventDispatcher
import com.ttstranslate.global.dispatcher.notifier.Notifier
import com.ttstranslate.observer.AppLifecycleObserver
import com.ttstranslate.domain.global.CoroutineProvider
import com.ttstranslate.domain.global.DispatcherProvider
import com.ttstranslate.global.utils.permissions.RecordAudioPermissionHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import com.ttstranslate.global.utils.permissions.StoragePermissionHandler

internal val appModule = module {
    factory { androidContext().resources }

    factory { AppLifecycleObserver(get()) }

    single { Notifier() }
    single { ErrorHandler(get(), get()) }
    single { EventDispatcher() }
    single { StoragePermissionHandler(androidContext()) }
    single { RecordAudioPermissionHandler(androidContext()) }

    single<CoroutineProvider> {
        object : CoroutineProvider {
            override val scopeIo
                get() = CoroutineScope(Dispatchers.IO)
            override val scopeMain
                get() = MainScope()
            override val scopeMainImmediate
                get() = CoroutineScope(Dispatchers.Main.immediate)
            override val scopeUnconfined
                get() = CoroutineScope(Dispatchers.Unconfined)
        }
    }

    single<DispatcherProvider> {
        object : DispatcherProvider {
            override val io: CoroutineDispatcher = Dispatchers.IO
            override val default = Dispatchers.Default
            override val ui = Dispatchers.Main
            override val unconfined = Dispatchers.Unconfined
        }
    }
}