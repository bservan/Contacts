package com.bservan.apps.contacts.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bservan.apps.contacts.domain.builtintest.Level
import com.bservan.apps.contacts.ui.state.LocalToastState
import kotlinx.coroutines.delay

@Composable
fun ToastHost() {
    val state = LocalToastState.current
    val message = state.message
    val level = message.value?.level ?: Level.INFORMATION

    AnimatedVisibility(
        visible = message.value != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        LaunchedEffect(message) {
            val durationMs = message.value?.duration
            val delayMs = durationMs ?: 1000L
            delay(delayMs)
            state.clear()
        }

        if (message.value != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 40.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = ToastConstants.getColor(level),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(ToastConstants.getColor(level), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ToastConstants.getIcon(level),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message.value?.message.orEmpty(),
                        color = ToastConstants.getColor(level),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private object ToastConstants {
    fun getColor(level: Level): Color {
        return when (level) {
            Level.INFORMATION -> Color.Green
            Level.WARNING -> Color.Yellow
            Level.ERROR -> Color.Red
        }
    }

    fun getIcon(level: Level): ImageVector {
        return when (level) {
            Level.INFORMATION -> Icons.Default.Check
            Level.WARNING -> Icons.Default.Warning
            Level.ERROR -> Icons.Default.Close
        }
    }
}
