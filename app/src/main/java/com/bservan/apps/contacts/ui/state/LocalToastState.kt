package com.bservan.apps.contacts.ui.state

import androidx.compose.runtime.staticCompositionLocalOf

val LocalToastState = staticCompositionLocalOf<ToastState> {
    error("No ToastState provided")
}
