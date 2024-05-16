package com.ab.contactsapp.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ab.contactsapp.domain.model.Contact
import com.ab.contactsapp.domain.model.toContactEntity
import com.ab.contactsapp.domain.repository.ContactDataSource
import com.ab.contactsapp.service.ContactService

class ContactPagingSource(
    private val contactService: ContactService, private val contactDataSource: ContactDataSource
): PagingSource<Int, Contact>() {


    override fun getRefreshKey(state: PagingState<Int, Contact>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Contact> {
        return try {
            val page = params.key ?: 1
            val contactList = contactService.getContactsInfo(page = page).results.map { it.toContactEntity() }
            contactDataSource.insertContact(contactList)
            LoadResult.Page(
                data = contactList,
                prevKey = if (page == 1) null else page.minus(1),
                nextKey = if (contactList.isEmpty()) null else page.plus(1),
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}