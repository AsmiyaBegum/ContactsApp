package com.ab.contactsapp.ui.contact_list

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ab.contactsapp.contactHelper.contact
import com.ab.contactsapp.contactHelper.getCallLogDetails
import com.ab.contactsapp.contactHelper.markContactAsFavorite
import com.ab.contactsapp.domain.model.CallLogEntry
import com.ab.contactsapp.domain.model.CallLogGroup
import com.ab.contactsapp.domain.model.Contact
import com.ab.contactsapp.domain.repository.ContactDataSource
import com.ab.contactsapp.utils.SearchContact
import com.ab.contactsapp.domain.usecase.ContactScreenUseCase
import com.ab.contactsapp.ui.base.BaseViewModel
import com.ab.contactsapp.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel  @Inject constructor(
    private val contactDataSource: ContactDataSource,
    private val useCase: ContactScreenUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val searchContact = SearchContact()
    val contactState: MutableState<Contact?> = mutableStateOf(null)

    private val _callLogEntries = MutableStateFlow<List<CallLogGroup>>(emptyList())
    val callLogEntries: Flow<List<CallLogGroup>> = _callLogEntries.asStateFlow()
    private val _showLoader = MutableStateFlow<Boolean>(false)
    val showLoader : Flow<Boolean> = _showLoader.asStateFlow()

    init {
        Log.d("recompose","recompose")
    }

    private val cachedRandomContacts = MutableStateFlow<Flow<PagingData<Contact>>>(flowOf(PagingData.empty()))

    init {
        deleteRandomContacts()
        fetchRandomContacts()
    }

    private fun fetchRandomContacts() {
        viewModelScope.launch {
            val randomContacts = useCase.getRemoteContacts().flow.cachedIn(viewModelScope)
            cachedRandomContacts.value = randomContacts
        }
    }

    fun getRandomContacts(): Flow<PagingData<Contact>> {
        return cachedRandomContacts.value
    }


    private fun deleteRandomContacts() {
        viewModelScope.launch {
            useCase.deleteRemoveContacts()
        }
    }

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
    private val isSearchActive = savedStateHandle.getStateFlow("isSearchActive", false)

    val state = combine(contacts, searchText,selectedTab,isSearchActive){ contacts, searchText,selectedTab,isSearchActive  ->

        ContactListState(
            contacts = if(selectedTab == Constants.PHONE_CONTACTS) {
                searchContact.searchLocalDeviceContacts(contacts,searchText)
            }else{
                contacts
            },
            searchText = searchText,
            selectedTab = selectedTab,
            isSearchActive = isSearchActive
        )

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactListState())


    fun loadcontacts(context: Context){
        _showLoader.value = true
        viewModelScope.launch(Dispatchers.IO) {
            if(selectedTab.value == Constants.PHONE_CONTACTS){
                savedStateHandle["contacts"] = contact(contentResolver = context.contentResolver)
            }
            _showLoader.value = false
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
            savedStateHandle["isSearchActive"] = false
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
        viewModelScope.launch(Dispatchers.IO) {
            markContactAsFavorite(contentResolver, contactId = contactId)
        }
    }



    fun deleteContact(contentResolver: ContentResolver, contactId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            // Specify the contact URI
            val contactUri: Uri = ContactsContract.Contacts.CONTENT_URI.buildUpon()
                .appendPath(contactId.toString()).build()

            // Delete the contact using the contact URI
            contentResolver.delete(contactUri, null, null)
        }
        savedStateHandle["contacts"] = listOf<Contact>()
        contactState.value = null
    }


    

    fun deleteNoteById(id : Long) {
        viewModelScope.launch {
//            contactDataSource.deleteNoteById(id)
//            loadcontacts()
        }
    }
}


