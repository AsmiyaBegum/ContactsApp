package com.ab.contactsapp.data.contact

import com.ab.contactsapp.data.data_source.ContactDatabase
import com.ab.contactsapp.domain.model.Contact
import com.ab.contactsapp.domain.repository.ContactDataSource
import javax.inject.Inject

class ContactDataSourceImpl@Inject constructor(private val database : ContactDatabase) :
    ContactDataSource {
    private val dao = database.contactsDao
    override suspend fun insertContact(contactList : List<Contact>) {
        dao.insertContactList(contactList)
    }

    override suspend fun deleteAllContact() {
        dao.deleteAllContact()
    }

}