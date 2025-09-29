package com.bservan.apps.contacts.ui.state.provider

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.bservan.apps.contacts.ui.state.LocalToastState
import com.bservan.apps.contacts.ui.state.ToastDispatcher
import com.bservan.apps.contacts.ui.state.rememberToastState

@Composable
fun ToastProvider(content: @Composable () -> Unit) {
    val toastState = rememberToastState()

    LaunchedEffect(Unit) {
        ToastDispatcher.messages.collect { message ->
            toastState.show(message)
        }
    }

    CompositionLocalProvider(LocalToastState provides toastState) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}
