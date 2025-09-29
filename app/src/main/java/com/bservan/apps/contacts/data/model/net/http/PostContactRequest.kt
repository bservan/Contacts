package com.bservan.apps.contacts.data.model.net.http

data class PostContactRequest(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val profileImageUrl: String? = null
)
