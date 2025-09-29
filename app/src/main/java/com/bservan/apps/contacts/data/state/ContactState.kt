package com.bservan.apps.contacts.data.state

import android.graphics.Bitmap

data class ContactState(
    val name: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val image: Bitmap? = null
)
