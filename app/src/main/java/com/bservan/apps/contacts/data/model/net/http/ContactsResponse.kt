package com.bservan.apps.contacts.data.model.net.http

data class ContactsResponse(
    val success: Boolean,
    val messages: List<String>?,
    val data: ContactsDetail,
    val status: Int
)
