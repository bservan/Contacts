package com.bservan.apps.contacts.data.model.builtintest

import com.bservan.apps.contacts.domain.builtintest.Level

data class ToastData(
    val message: String,
    val level: Level = Level.INFORMATION,
    val duration: Long = 1000L // One second
)