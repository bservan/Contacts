package com.bservan.apps.contacts.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bservan.apps.contacts.R
import com.bservan.apps.contacts.ui.theme.ContactsTheme

@Composable
fun ContactAddDoneScreen() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.done
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(3000f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LottieAnimation(
                composition = composition,
                iterations = 1,
                modifier = Modifier.size(150.dp)
            )
            Text(
                text = "All Done!",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                buildAnnotatedString {
                    append("New contact saved ")
                    withStyle(style = SpanStyle(fontSize = 24.sp)) {
                        append("\uD83C\uDF89")
                    }
                }
            )
        }
    }
}
