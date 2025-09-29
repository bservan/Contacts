package com.bservan.apps.contacts.domain.net.http

import android.graphics.Bitmap
import com.bservan.apps.contacts.data.model.net.http.PostContactRequest
import com.bservan.apps.contacts.data.model.net.http.ContactResponse
import com.bservan.apps.contacts.data.model.net.http.ContactsResponse
import com.bservan.apps.contacts.data.model.net.http.UploadImageResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ContactService {
    @GET("api/User/GetAll")
    suspend fun getContacts(): Response<ContactsResponse>

    @GET("api/User/{id}")
    suspend fun getContactById(
        @Path("id") id: String
    ): Response<ContactResponse>

    @POST("api/User")
    suspend fun addContact(
        @Body contact: PostContactRequest
    ): Response<ContactResponse>

    @PUT("api/User/{id}")
    suspend fun updateContact(
        @Body contact: PostContactRequest,
        @Path("id") id: String
    ): Response<ContactResponse>

    @DELETE("api/User/{id}")
    suspend fun deleteContact(
        @Path("id") id: String
    ): Response<ContactResponse>

    @Multipart
    @POST("api/User/UploadImage")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponse>
}