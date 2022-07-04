package com.ttstranslate.domain.global

import kotlinx.coroutines.CoroutineScope

interface CoroutineProvider {
    val scopeIo: CoroutineScope
    val scopeMain: CoroutineScope
    val scopeMainImmediate: CoroutineScope
    val scopeUnconfined: CoroutineScope
}
