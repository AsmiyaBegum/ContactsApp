package com.ab.contactsapp.domain.contact

import com.ab.contactsapp.utils.sortByCustomOrder

class SearchContact {

    fun execute(
        contacts: List<ContactInfo>,
        query: String,
    ): List<ContactInfo> {
        if (query.isBlank()) {
            return contacts
        }
        return contacts.filter {
            it.name.first.lowercase().contains(query.lowercase()) ||
                    it.name.last.lowercase().contains(query.lowercase()) ||
                    it.name.title.lowercase().contains(query.lowercase()) ||
                    it.phone.lowercase().contains(query.lowercase()) ||
                    it.cell.lowercase().contains(query.lowercase()) ||
                    it.email.lowercase().contains(query.lowercase())
        }.sortByCustomOrder {
            it.name.first
        }
    }


    fun searchLocalDeviceContacts(
        contacts: List<Contact>,
        query: String
    ) : List<Contact>{
        if (query.isBlank()) {
            return contacts
        }
        return contacts.filter {
            (it.name?:"").lowercase().contains(query.lowercase()) ||
                    (it.phoneNumber?:"").lowercase().contains(query.lowercase()) ||
                    (it.email?:"").lowercase().contains(query.lowercase())
        }.sortByCustomOrder {
            it.name?:""
        }
    }
}
