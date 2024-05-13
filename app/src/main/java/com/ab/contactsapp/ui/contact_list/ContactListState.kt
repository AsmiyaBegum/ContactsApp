package com.ab.contactsapp.ui.contact_list

import com.ab.contactsapp.domain.contact.Contact
import com.ab.contactsapp.domain.contact.ContactInfo
import com.ab.contactsapp.utils.Constants

data class ContactListState (
    val contacts : List<Contact>  = emptyList(),
    val searchText : String = "",
    val randomContact: List<Contact> = emptyList(),
    val selectedTab : String = Constants.PHONE_CONTACTS,
    val isSearchActive : Boolean = false
)