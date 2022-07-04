package com.ttstranslate.global.utils.permissions

import android.Manifest
import android.content.Context
import android.os.Build

@Suppress("unused")
class StoragePermissionHandler(context: Context) : PermissionHandler(context) {
    override val permissions: List<String>
        get() = listOfNotNull(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
                .takeUnless { Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q },
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
}
