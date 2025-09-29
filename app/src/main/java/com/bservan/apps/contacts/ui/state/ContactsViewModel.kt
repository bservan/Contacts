package com.bservan.apps.contacts.ui.state

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bservan.apps.contacts.data.model.Contact
import com.bservan.apps.contacts.data.model.db.dao.ContactDao
import com.bservan.apps.contacts.data.model.net.http.ContactDetail
import com.bservan.apps.contacts.data.model.net.http.PostContactRequest
import com.bservan.apps.contacts.data.model.toContact
import com.bservan.apps.contacts.data.state.ContactState
import com.bservan.apps.contacts.domain.repository.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val repository: ContactRepository,
    private val dao: ContactDao
) : ViewModel() {
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun getContacts() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            repository.getContacts()
                .onSuccess { contactList ->
                    _contacts.value = contactList.map { it.toContact() }
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }

            _loading.value = false
        }
    }

    fun getContactById(id: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            repository.getContactById(id)
                .onSuccess { contact ->
                    _contacts.value = _contacts.value.map {
                        if (it.id == id) contact.toContact() else it
                    }
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }

            _loading.value = false
        }
    }

    fun createContact(firstName: String, lastName: String, phoneNumber: String, image: Bitmap?) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            val contactRequest = PostContactRequest(
                firstName,
                lastName,
                phoneNumber,
                null
            )

            repository.createContact(contactRequest)
                .onSuccess { contact ->
                    if (image != null) {
                        repository.uploadImage(image)
                            .onSuccess { imageUrl ->
                                val contactWithImage = contact.copy(
                                    profileImageUrl = imageUrl
                                )
                                repository.updateContact(
                                    contact.id,
                                    contactRequest.copy(
                                        profileImageUrl = imageUrl
                                    )
                                )
                                _contacts.value = _contacts.value + contactWithImage.toContact()
                            }
                            .onFailure { exception ->
                                _error.value = exception.message
                            }
                    } else {
                        _contacts.value = _contacts.value + contact.toContact()
                    }
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }

            _loading.value = false
        }
    }

    fun deleteContact(id: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            repository.deleteContact(id)
                .onSuccess { contact ->
                    _contacts.value = _contacts.value.filter { it.id != id }
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }

            _loading.value = false
        }
    }

    fun updateContact(id: String,
                      contactRequest: PostContactRequest,
                      image: Bitmap? = null) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            repository.updateContact(id, contactRequest)
                .onSuccess { contact ->
                    if (image != null) {
                        repository.uploadImage(image)
                            .onSuccess { imageUrl ->
                                val contactWithImage = contact.copy(
                                    profileImageUrl = imageUrl
                                )
                                _contacts.value = _contacts.value.map {
                                    if (it.id == id) contactWithImage.toContact() else it
                                }
                            }
                            .onFailure { exception ->
                                _error.value = exception.message
                            }
                    } else {
                        _contacts.value = _contacts.value.map {
                            if (it.id == id) contact.toContact() else it
                        }
                    }
                }
                .onFailure { exception ->

                }

            _loading.value = false
        }
    }
}
