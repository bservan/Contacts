package com.bservan.apps.contacts.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.bservan.apps.contacts.data.model.builtintest.ToastData

@Composable
fun rememberToastState(): ToastState {
    return remember { ToastState() }
}

class ToastState {
    var message = mutableStateOf<ToastData?>(null)
        private set

    fun show(msg: ToastData) {
        message.value = msg
    }

    fun clear() {
        message.value = null
    }
}
