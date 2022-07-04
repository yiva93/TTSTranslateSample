package com.ttstranslate.global.utils.permissions

import android.Manifest
import android.content.Context

class RecordAudioPermissionHandler(context: Context) : PermissionHandler(context) {
    override val permissions: List<String>
        get() = listOf(
            Manifest.permission.RECORD_AUDIO
        )
}