package com.ab.contactsapp.ui.contact_create

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ab.contactsapp.contactHelper.editContact
import com.ab.contactsapp.domain.model.Contact
import com.ab.contactsapp.utils.ContactInfoArgType
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactCreationViewmodel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel(){

    private val firstName = savedStateHandle.getStateFlow("firstName", "")
    private val lastName = savedStateHandle.getStateFlow("lastName", "")
    private val phone = savedStateHandle.getStateFlow("phone", "")
    private val email = savedStateHandle.getStateFlow("email", "")
    private val photo : StateFlow<ByteArray?> = savedStateHandle.getStateFlow("photo", null)

     var contact : Contact? = null

    val state = combine(
        firstName,
        lastName,
        phone,
        email,
        photo

    ) {   firstName, lastName, phone, email,photo ->

        CreateContactState(
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            email = email,
            photo = photo
        )

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CreateContactState())


    private val _hasContactBeenSaved = MutableStateFlow(false)
    val hasContactBeenSaved = _hasContactBeenSaved.asStateFlow()

    var existingContactId : Long = -1


    init {
        val contact = ContactInfoArgType().fromJsonParse(savedStateHandle.get<String>("contact")?: Gson().toJson(Contact()))
        contact?.let{ contact ->
            viewModelScope.launch(Dispatchers.IO) {
                existingContactId = contact.contactId.toLongOrNull()?:-1L
                val multipleNames = contact.name?.split(" ")?.size?:0
                savedStateHandle["firstName"] = if(multipleNames > 1) contact.name?.split(" ")?.take(multipleNames-1)?.joinToString(" ") else contact.name
                savedStateHandle["lastName"] = if(multipleNames > 1) contact.name?.split(" ")?.lastOrNull() else ""
                savedStateHandle["phone"] = contact.phoneNumber
                savedStateHandle["email"] = contact.email
                savedStateHandle["photo"] = contact.photo
            }
        }
    }


    fun onFirstNameChanged(firstName : String){
        savedStateHandle["firstName"] = firstName
    }


    fun onLastNameChanged(lastName : String){
        savedStateHandle["lastName"] = lastName
    }

   fun onPhoneNumberChanged(phoneNumber : String){
       savedStateHandle["phone"] = phoneNumber
   }

   fun onEmailChanged(email : String){
       savedStateHandle["email"] = email
   }

  fun onPhotoChanged(byteArray: ByteArray?){
      savedStateHandle["photo"] = byteArray
  }


    fun saveContact(context : Context) {
        viewModelScope.launch(Dispatchers.IO) {
            contact =  Contact(
                name = state.value.firstName,
                lastName = state.value.lastName,
                phoneNumber = state.value.phone,
                email = state.value.email,
                photo = state.value.photo,
               contactId =  existingContactId.toString()
            )
            if(existingContactId!=-1L){
                editContact(context, contact!!)
            }else{
                com.ab.contactsapp.contactHelper.saveContact(context,
                    contact!!
                )
            }

        }
        _hasContactBeenSaved.value = true
    }
}