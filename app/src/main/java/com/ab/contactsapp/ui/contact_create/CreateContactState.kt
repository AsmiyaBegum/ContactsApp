package com.ab.contactsapp.ui.contact_create

data class CreateContactState(
    val firstName : String = "",
    val lastName : String = "",
    val phone : String = "",
    val email : String = "",
    val photo : ByteArray? = null
)