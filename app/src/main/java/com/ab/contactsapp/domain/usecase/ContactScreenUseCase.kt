package com.ab.contactsapp.domain.usecase

import androidx.paging.Pager
import com.ab.contactsapp.data.repository.ContactRepository
import com.ab.contactsapp.domain.model.Contact
import javax.inject.Inject

class ContactScreenUseCase @Inject constructor(private val contactScreenRepository: ContactRepository) {

    fun getRemoteContacts() : Pager<Int, Contact> {
        return contactScreenRepository.getRemotePagingContacts()
    }

    suspend fun deleteRemoveContacts() {
        contactScreenRepository.deleteContactsFromDatabase()
    }

}