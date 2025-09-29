package com.bservan.apps.contacts.data.model

import com.bservan.apps.contacts.data.model.db.ContactEntity
import com.bservan.apps.contacts.data.model.net.http.ContactDetail
import com.bservan.apps.contacts.data.state.ContactState
import com.bservan.apps.contacts.domain.content.bytesToBitmap

fun ContactEntity.toContact(): Contact {
    return Contact(
        id = internalId,
        state = ContactState(
            name = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            image = bytesToBitmap(imageData, maxWidth = 256, maxHeight = 256)
        )
    )
}

fun ContactDetail.toContact(): Contact {
    return Contact(
        id = id,
        state = ContactState(
            name = firstName ?: "",
            lastName = lastName ?: "",
            phoneNumber = phoneNumber,
            image = null
        )
    )
}
