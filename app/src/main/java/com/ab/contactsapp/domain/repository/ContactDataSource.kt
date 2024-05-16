package com.ab.contactsapp.domain.repository

import com.ab.contactsapp.domain.model.Contact


interface ContactDataSource {
    suspend fun insertContact(contactList : List<Contact>)

    suspend fun deleteAllContact()

}

