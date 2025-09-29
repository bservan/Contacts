package com.bservan.apps.contacts.domain.repository

import android.graphics.Bitmap
import com.bservan.apps.contacts.data.model.net.http.ContactDetail
import com.bservan.apps.contacts.data.model.net.http.PostContactRequest

interface ContactRepository {
    suspend fun getContacts(): Result<List<ContactDetail>>
    suspend fun getContactById(id: String): Result<ContactDetail>
    suspend fun createContact(request: PostContactRequest): Result<ContactDetail>
    suspend fun updateContact(id: String, request: PostContactRequest): Result<ContactDetail>
    suspend fun deleteContact(id: String): Result<ContactDetail>
    suspend fun uploadImage(image: Bitmap): Result<String>
}