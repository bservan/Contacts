package com.bservan.apps.contacts.data.model

import com.bservan.apps.contacts.data.state.ContactState

data class Contact (
    val state: ContactState,
    val id: String
) : Comparable<Contact> {
    override fun compareTo(other: Contact): Int {
        val fullName = "${state.name} ${state.lastName}"
        val othersFullName = "${other.state.name} ${other.state.lastName}"
        return fullName.compareTo(othersFullName)
    }
}
