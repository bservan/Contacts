package com.bservan.apps.contacts.data.model.net.http

data class ContactDetail(
    val id: String,
    val createdAt: String,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String,
    val profileImageUrl: String?
)
