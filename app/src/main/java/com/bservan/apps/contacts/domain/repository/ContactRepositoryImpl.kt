package com.bservan.apps.contacts.domain.repository

import android.graphics.Bitmap
import com.bservan.apps.contacts.data.model.net.http.ContactDetail
import com.bservan.apps.contacts.data.model.net.http.PostContactRequest
import com.bservan.apps.contacts.domain.content.toMultipartPart
import com.bservan.apps.contacts.domain.net.http.ContactApiClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.Result

class ContactRepositoryImpl : ContactRepository {
    private val service = ContactApiClient.service
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("Error: ${throwable.localizedMessage}")
    }

    override suspend fun getContacts(): Result<List<ContactDetail>> = withContext(Dispatchers.IO + exceptionHandler) {
        try {
            val response = service.getContacts()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val list = mutableListOf<ContactDetail>()
                    body.data.users.forEach {
                        list.add(it)
                    }
                    Result.success(list)
                } else {
                    Result.failure(Exception("Failed to get contacts: ${response.code()}"))
                }
            } else {
                Result.failure(Exception("Failed to get contacts: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getContactById(id: String): Result<ContactDetail> = withContext(Dispatchers.IO + exceptionHandler) {
        try {
            val response = service.getContactById(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.status < 400) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Failed to get contact: ${response.code()}"))
                }
            } else {
                Result.failure(Exception("Failed to get contact: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createContact(request: PostContactRequest): Result<ContactDetail> = withContext(Dispatchers.IO + exceptionHandler) {
        try {
            val response = service.addContact(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.status < 400) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Failed to get contact: ${response.code()}"))
                }
            } else {
                Result.failure(Exception("Failed to get contact: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateContact(
        id: String,
        request: PostContactRequest
    ): Result<ContactDetail> = withContext(Dispatchers.IO + exceptionHandler) {
        try {
            val response = service.updateContact(request, id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.status < 400) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Failed to update contact: ${response.code()}"))
                }
            } else {
                Result.failure(Exception("Failed to update contact: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteContact(id: String): Result<ContactDetail> = withContext(Dispatchers.IO + exceptionHandler) {
        try {
            val response = service.deleteContact(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.status < 400) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Failed to delete contact: ${response.code()}"))
                }
            } else {
                Result.failure(Exception("Failed to delete contact: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadImage(image: Bitmap): Result<String> = withContext(Dispatchers.IO + exceptionHandler) {
        try {
            val response = service.uploadImage(image.toMultipartPart(quality = 50, format = Bitmap.CompressFormat.PNG))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.status < 400) {
                    Result.success(body.data.imageUrl)
                } else {
                    Result.failure(Exception("Failed to upload image: ${response.code()}"))
                }
            } else {
                Result.failure(Exception("Failed to upload image: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}