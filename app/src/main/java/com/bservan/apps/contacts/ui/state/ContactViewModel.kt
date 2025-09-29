package com.bservan.apps.contacts.ui.state

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.bservan.apps.contacts.data.state.ContactState

class ContactViewModel : ViewModel() {
    var state by mutableStateOf(ContactState("", "", ""))
        private set

    fun clear() {
        state = ContactState()
    }

    fun onNameChanged(newName: String) {
        state = state.copy(
            name = newName
        )
    }

    fun onLastNameChanged(newLastName: String) {
        state = state.copy(
            lastName = newLastName
        )
    }

    fun onPhoneNumberChanged(newPhoneNumber: String) {
        state = state.copy(
            phoneNumber = newPhoneNumber
        )
    }

    fun onImageChanged(newImage: Bitmap) {
        state = state.copy(
            image = newImage
        )
    }
}