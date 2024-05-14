package com.ab.contactsapp.ui.contact_list

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ab.contactsapp.contactHelper.contact
import com.ab.contactsapp.contactHelper.getCallLogDetails
import com.ab.contactsapp.contactHelper.markContactAsFavorite
import com.ab.contactsapp.domain.contact.CallLogEntry
import com.ab.contactsapp.domain.contact.CallLogGroup
import com.ab.contactsapp.domain.contact.Contact
import com.ab.contactsapp.domain.contact.ContactInfo
import com.ab.contactsapp.domain.contact.ContactDataSource
import com.ab.contactsapp.domain.contact.SearchContact
import com.ab.contactsapp.domain.contact.mockContacts
import com.ab.contactsapp.ui.base.BaseViewModel
import com.ab.contactsapp.utils.Constants
import com.ab.contactsapp.utils.sortByCustomOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel  @Inject constructor(
    private val contactDataSource: ContactDataSource,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val searchContact = SearchContact()
    val contactState: MutableState<Contact?> = mutableStateOf(null)

    private val _callLogEntries = MutableStateFlow<List<CallLogGroup>>(emptyList())
    val callLogEntries: Flow<List<CallLogGroup>> = _callLogEntries.asStateFlow()

    fun getCallLogForNumber(contentResolver: ContentResolver, phoneNumber: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val callLog =  getCallLogDetails(contentResolver, phoneNumber)
                _callLogEntries.value = groupCallLogEntriesByDate(callLog)
            }

        }
    }

   private fun groupCallLogEntriesByDate(callLogEntries: List<CallLogEntry>): List<CallLogGroup> {
        return callLogEntries.groupBy { entry ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = entry.date
            // Truncate the time part to group by date only
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }.map { (date, entries) ->
            CallLogGroup(date, entries)
        }.sortedByDescending { it.date }
    }



    fun updateSelectedContact(selectedContact : Contact) {
        viewModelScope.launch {
            contactState.value = selectedContact
        }
    }



    private val contacts = savedStateHandle.getStateFlow("contacts", emptyList<Contact>())
    private val searchText = savedStateHandle.getStateFlow("searchText", "")
    private val selectedTab = savedStateHandle.getStateFlow("selectedTab", Constants.PHONE_CONTACTS)
    private val randomContact = savedStateHandle.getStateFlow("randomContacts", emptyList<Contact>())
    private val isSearchActive = savedStateHandle.getStateFlow("isSearchActive", false)

    val state = combine(contacts, searchText,randomContact,selectedTab,isSearchActive){ contacts, searchText,randomContacts,selectedTab,isSearchActive  ->

        ContactListState(
            contacts = if(selectedTab == Constants.PHONE_CONTACTS) {
                searchContact.searchLocalDeviceContacts(contacts,searchText)
            }else{
                contacts
            },
            searchText = searchText,
            randomContact = if(selectedTab == Constants.RANDOM_CONTACTS) {
                searchContact.searchLocalDeviceContacts(randomContacts,searchText)
            }else{
                randomContacts
            },
            selectedTab = selectedTab,
            isSearchActive = isSearchActive
        )

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactListState())


    fun loadcontacts(context: Context){
        viewModelScope.launch(Dispatchers.IO) {
            if(selectedTab.value == Constants.PHONE_CONTACTS){
                savedStateHandle["contacts"] = contact(contentResolver = context.contentResolver)
            }else{
                savedStateHandle["randomContacts"] = mockContacts.sortByCustomOrder { it.name?:"" }
            }
        }
    }

    fun onSearchTextChanges(text : String) {
        viewModelScope.launch {
            savedStateHandle["searchText"] = text
        }
    }

    fun onTabSelected(tabIndex : Int){
        viewModelScope.launch {
            savedStateHandle["searchText"] = ""
         val tabName = if (tabIndex == 0) Constants.PHONE_CONTACTS else Constants.RANDOM_CONTACTS
            savedStateHandle["selectedTab"] = tabName
        }
    }


    fun onToggleSearch() {
        savedStateHandle["isSearchActive"] = !isSearchActive.value
        if(!isSearchActive.value){
            savedStateHandle["searchText"] = ""

        }
    }

    fun markContactAsFav(contentResolver: ContentResolver,contactId: Long){
        markContactAsFavorite(contentResolver, contactId = contactId)
    }


    fun deleteContact(contentResolver: ContentResolver, contactId: Long) {

        // Specify the contact URI
        val contactUri: Uri = ContactsContract.Contacts.CONTENT_URI.buildUpon()
            .appendPath(contactId.toString()).build()

        // Delete the contact using the contact URI
        contentResolver.delete(contactUri, null, null)
    }


    

    fun deleteNoteById(id : Long) {
        viewModelScope.launch {
//            contactDataSource.deleteNoteById(id)
//            loadcontacts()
        }
    }
}


