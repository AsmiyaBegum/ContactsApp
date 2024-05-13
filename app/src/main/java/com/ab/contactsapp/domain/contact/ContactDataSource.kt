package com.ab.contactsapp.domain.contact


interface ContactDataSource {
    suspend fun insertContact(contactList : List<Contact>)

    suspend fun getAllContacts() : List<Contact>

    suspend fun deleteAllContact()

}

