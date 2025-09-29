package com.bservan.apps.contacts.data.model.net.http

data class ContactResponse(
    val success: Boolean,
    val messages: List<String>?,
    val data: ContactDetail,
    val status: Int
)
