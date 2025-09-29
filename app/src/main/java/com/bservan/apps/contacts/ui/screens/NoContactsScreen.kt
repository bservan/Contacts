package com.bservan.apps.contacts.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bservan.apps.contacts.R
import com.bservan.apps.contacts.ui.theme.ContactsBlue
import com.bservan.apps.contacts.ui.theme.ContactsPhotoPlaceholder

@Composable
fun NoContactsScreen(action: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(R.dimen.no_contacts_padding_top)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_contact_placeholder),
            tint = ContactsPhotoPlaceholder,
            contentDescription = null
        )
        Text(
            text = "No Contacts",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
        Text(
            text = "Contacts you've added will appear here."
        )
        TextButton(
            onClick = {
                action()
            },
            colors = ButtonDefaults.textButtonColors().copy(
                contentColor = ContactsBlue,
                disabledContentColor = Color.Gray
            )
        ) {
            Text(
                text = "Create New Contact",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
