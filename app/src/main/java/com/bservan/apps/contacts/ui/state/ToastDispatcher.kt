package com.bservan.apps.contacts.ui.state

import com.bservan.apps.contacts.data.model.builtintest.ToastData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object ToastDispatcher {
    private val _messages = MutableSharedFlow<ToastData>(extraBufferCapacity = 4)
    val messages = _messages.asSharedFlow()

    fun show(message: String) = _messages.tryEmit(ToastData(message))
    fun show(message: ToastData) = _messages.tryEmit(message)
}
