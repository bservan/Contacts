package com.bservan.apps.contacts.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bservan.apps.contacts.ui.theme.Typography
import com.bservan.apps.contacts.R
import com.bservan.apps.contacts.ui.state.ContactViewModel
import com.bservan.apps.contacts.ui.state.ContactsViewModel
import com.bservan.apps.contacts.ui.theme.ContactsBlue
import com.bservan.apps.contacts.ui.widget.BottomPanel
import com.bservan.apps.contacts.ui.widget.SearchableContactList
import androidx.compose.runtime.collectAsState
import com.bservan.apps.contacts.ui.state.ToastDispatcher

@Composable
fun MainScreen(modifier: Modifier, viewModel: ContactsViewModel) {
    var show by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit, viewModel.contacts.collectAsState().value) {
        viewModel.getContacts()
    }

    Box(modifier = modifier) {
        Column {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_top)))
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Contacts",
                    style = Typography.titleLarge
                )
                IconButton(onClick = {
                    show = true
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add_contact),
                        contentDescription = "Add New Contact",
                        tint = ContactsBlue
                    )
                }
            }

            if (viewModel.contacts.collectAsState().value.size > 0) {
                SearchableContactList(viewModel)
            } else {
                NoContactsScreen {
                    show = true
                }
            }
        }
        BottomPanel(visible = show, onDismiss = { show = false }, heightRatio = 0.95f) {
            FillNewContactScreen(
                contactsViewModel = viewModel,
                viewModel = viewModel<ContactViewModel>(),
                onDismiss = {
                    show = false
                }
            )
        }
    }
}
