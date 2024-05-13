package com.ab.contactsapp.contactHelper

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.CallLog
import android.provider.ContactsContract
import com.ab.contactsapp.domain.contact.CallLogEntry
import com.ab.contactsapp.domain.contact.Contact
import java.io.ByteArrayOutputStream


fun getContacts(contentResolver: ContentResolver): List<Contact> {
    val contacts = mutableListOf<Contact>()
    val cursor = contentResolver.query(
        ContactsContract.Data.CONTENT_URI,
        arrayOf(
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Photo.PHOTO
        ),
        "${ContactsContract.Data.MIMETYPE} = ?",
        arrayOf(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE),
        null
    )
    cursor?.use {
        val contactIdColumnIndex = it.getColumnIndex(ContactsContract.Data.CONTACT_ID)
        val nameColumnIndex = it.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY)
        val phoneNumberColumnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val emailColumnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
        val photoColumnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO)


        while (it.moveToNext()) {
            val contactId = if (contactIdColumnIndex != -1) {
                it.getString(contactIdColumnIndex)
            } else {
                // Handle case where column index is -1 (not found)
                null
            }
            val name = if (nameColumnIndex != -1) {
                it.getString(nameColumnIndex)
            } else {
                // Handle case where column index is -1 (not found)
                null
            }
            val phoneNumber = if (phoneNumberColumnIndex != -1) {
                it.getString(phoneNumberColumnIndex)
            } else {
                // Handle case where column index is -1 (not found)
                null
            }
            val email = if (emailColumnIndex != -1) {
                it.getString(emailColumnIndex)
            } else {
                // Handle case where column index is -1 (not found)
                null
            }

            val photoByteArray = if(contactId!=null){
                val photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId.toLong())
                val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, photoUri)
                bitmapToByteArray(inputStream?.use(BitmapFactory::decodeStream))
            }else{
                null
            }




            // Add error handling and add the contact to the list
            if (contactId != null) {
                contacts.add(Contact(name, phoneNumber, email, contactId,photoByteArray))
            }
        }
    }
    cursor?.close()
    return contacts
}
 private fun getContactPhoto(context: Context, contactId: Long): Bitmap? {
    val photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId)
    val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.contentResolver, photoUri)
    return inputStream?.use(BitmapFactory::decodeStream)
}

fun getCallLogDetails(contentResolver: ContentResolver, phoneNumber: String): List<CallLogEntry> {
    val callLogList = mutableListOf<CallLogEntry>()

    val cursor = contentResolver.query(
        CallLog.Calls.CONTENT_URI,
        null,
        CallLog.Calls.NUMBER + " = ?",
        arrayOf(phoneNumber),
        null
    )

    cursor?.use { cursor ->
        val idColumn = cursor.getColumnIndex(CallLog.Calls._ID)
        val numberColumn = cursor.getColumnIndex(CallLog.Calls.NUMBER)
        val dateColumn = cursor.getColumnIndex(CallLog.Calls.DATE)
        val durationColumn = cursor.getColumnIndex(CallLog.Calls.DURATION)
        val typeColumn = cursor.getColumnIndex(CallLog.Calls.TYPE)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val number = cursor.getString(numberColumn)
            val date = cursor.getLong(dateColumn)
            val duration = cursor.getLong(durationColumn)
            val type = cursor.getInt(typeColumn)

            val callType = when (type) {
                CallLog.Calls.INCOMING_TYPE -> "Incoming"
                CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                CallLog.Calls.MISSED_TYPE -> "Missed"
                else -> "Unknown"
            }

            val callLogEntry = CallLogEntry(id, number, date, duration, callType)
            callLogList.add(callLogEntry)
        }
    }

    return callLogList
}

fun markContactAsFavorite(contentResolver: ContentResolver, contactId: Long) {


    // Prepare the content values to update the contact
    val contentValues = ContentValues().apply {
        put(ContactsContract.Contacts.STARRED, 1) // 1 indicates favorite, 0 indicates not favorite
    }

    // Update the contact in the contacts database
    val selection = "${ContactsContract.Contacts._ID} = ?"
    val selectionArgs = arrayOf(contactId.toString())

    contentResolver.update(
        ContactsContract.Contacts.CONTENT_URI,
        contentValues,
        selection,
        selectionArgs
    )
}

fun bitmapToByteArray(bitmap: Bitmap?, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 100): ByteArray? {
    if(bitmap == null)
        return null
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(format, quality, outputStream)
    return outputStream.toByteArray()
}
