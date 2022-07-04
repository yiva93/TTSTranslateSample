package com.ttstranslate.global.utils.dialog.alert

import androidx.appcompat.app.AlertDialog

data class AlertDialogAction(
    val text: String,
    val callback: (dialog: AlertDialog) -> Unit
)
