package com.ab.contactsapp.utils

import com.ab.contactsapp.domain.model.Contact

class SearchContact {

    fun searchLocalDeviceContacts(
        contacts: List<Contact>,
        query: String
    ): List<Contact> {
        if (query.isBlank()) {
            return contacts
        }
        return contacts.filter {
            (it.name ?: "").lowercase().contains(query.lowercase()) ||
                    (it.phoneNumber ?: "").lowercase().contains(query.lowercase()) ||
                    (it.email ?: "").lowercase().contains(query.lowercase())
        }.sortByCustomOrder {
            it.name ?: ""
        }
    }
}
