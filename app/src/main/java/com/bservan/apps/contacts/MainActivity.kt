package com.bservan.apps.contacts

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.bservan.apps.contacts.activity.FullscreenActivity
import com.bservan.apps.contacts.data.model.db.ContactsDatabase
import com.bservan.apps.contacts.domain.repository.ContactRepositoryImpl
import com.bservan.apps.contacts.ui.screens.MainScreen
import com.bservan.apps.contacts.ui.state.ContactsViewModel
import com.bservan.apps.contacts.ui.state.provider.ToastProvider
import com.bservan.apps.contacts.ui.theme.ContactsTheme
import com.bservan.apps.contacts.ui.widget.ToastHost

class MainActivity : FullscreenActivity() {
    private lateinit var contactsViewModel: ContactsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contactsViewModel = ContactsViewModel(
            ContactRepositoryImpl(),
            ContactsDatabase.getDatabase(applicationContext).contactDao()
        )

        enableEdgeToEdge()
        setContent {
            ContactsTheme {
                ToastProvider {
                    MainScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = contactsViewModel
                    )
                    ToastHost()
                }
            }
        }
    }
}
