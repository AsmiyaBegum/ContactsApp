package com.ab.contactsapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.ab.contactsapp.domain.repository.ContactDataSource
import com.ab.contactsapp.service.ContactService
import javax.inject.Inject

class ContactRepository @Inject constructor(private val contactService: ContactService, private  val contactDataSource: ContactDataSource) {

    fun getRemotePagingContacts() =
        Pager(
            config = PagingConfig(
                pageSize = 10,
            ),
            pagingSourceFactory = {
                ContactPagingSource(contactService, contactDataSource)
            }
        )

    suspend fun deleteContactsFromDatabase() {
        contactDataSource.deleteAllContact()
    }
}