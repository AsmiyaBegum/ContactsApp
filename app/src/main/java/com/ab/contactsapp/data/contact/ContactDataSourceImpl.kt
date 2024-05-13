package com.ab.contactsapp.data.contact

import com.ab.contactsapp.data.data_source.ContactDatabase
import com.ab.contactsapp.domain.contact.Contact
import com.ab.contactsapp.domain.contact.ContactDataSource
import com.ab.contactsapp.domain.contact.ContactInfo
import javax.inject.Inject

class ContactDataSourceImpl@Inject constructor(private val database : ContactDatabase) : ContactDataSource {
    private val dao = database.contactsDao
    override suspend fun insertContact(contactList : List<Contact>) {
        dao.insertContactList(contactList)
    }

    override suspend fun getAllContacts(): List<Contact> {
       return dao.getContact()
    }

    override suspend fun deleteAllContact() {
        dao.deleteAllContatct()
    }

}