package com.bservan.apps.contacts.data.model.net.http

data class UploadImageResponse(
    val success: Boolean,
    val messages: List<String>?,
    val data: UploadImageDetail,
    val status: Int
)
