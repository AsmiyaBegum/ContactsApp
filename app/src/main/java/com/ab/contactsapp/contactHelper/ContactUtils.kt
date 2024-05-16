package com.ab.contactsapp.contactHelper

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import com.ab.contactsapp.domain.model.CallLogEntry
import com.ab.contactsapp.domain.model.Contact
import com.ab.contactsapp.utils.Utils
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Collections

private const val DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
private val PROJECTION = arrayOf(
    ContactsContract.Data.CONTACT_ID,
    ContactsContract.Contacts.HAS_PHONE_NUMBER,
    ContactsContract.Data.DISPLAY_NAME,
    ContactsContract.Data.DATA1,
    ContactsContract.Data.MIMETYPE
)
private const val ORDER = DISPLAY_NAME
private const val selection = ContactsContract.Data.MIMETYPE + " = ?" +
        " OR " + ContactsContract.Data.MIMETYPE + " = ?" + " OR " + ContactsContract.Data.MIMETYPE + " = ?"
private val selectionArgs = arrayOf(
    "%" + "@" + "%",
    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
)

//fun getContacts(contentResolver: ContentResolver): List<Contact> {
//    val contacts = mutableListOf<Contact>()
//    val cursor = contentResolver.query(
//        ContactsContract.Data.CONTENT_URI,
//        arrayOf(
//            ContactsContract.Data.CONTACT_ID,
//            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
//            ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
//            ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
//            ContactsContract.CommonDataKinds.Phone.NUMBER,
//            ContactsContract.CommonDataKinds.Email.ADDRESS,
//            ContactsContract.CommonDataKinds.Photo.PHOTO
//        ),
//        "${ContactsContract.Data.MIMETYPE} = ?",
//        arrayOf(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE),
//        null
//    )
//    cursor?.use {
//        val contactIdColumnIndex = it.getColumnIndex(ContactsContract.Data.CONTACT_ID)
//        val nameColumnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)
//        val lastNameColumnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)
//        val phoneNumberColumnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
//        val emailColumnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
//        val photoColumnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO)
//
//
//        while (it.moveToNext()) {
//            val contactId = if (contactIdColumnIndex != -1) {
//                it.getString(contactIdColumnIndex)
//            } else {
//                // Handle case where column index is -1 (not found)
//                null
//            }
//            val name = if (nameColumnIndex != -1) {
//                it.getString(nameColumnIndex)
//            } else {
//                // Handle case where column index is -1 (not found)
//                null
//            }
//
//            val lastName = if (lastNameColumnIndex != -1) {
//                it.getString(lastNameColumnIndex)
//            } else {
//                // Handle case where column index is -1 (not found)
//                null
//            }
//
//            val phoneNumber = if (phoneNumberColumnIndex != -1) {
//                it.getString(phoneNumberColumnIndex)
//            } else {
//                // Handle case where column index is -1 (not found)
//                null
//            }
//            val email = if (emailColumnIndex != -1) {
//                it.getString(emailColumnIndex)
//            } else {
//                // Handle case where column index is -1 (not found)
//                null
//            }
//
//            val photoByteArray = if(contactId!=null){
//                val photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId.toLong())
//                val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, photoUri)
//                bitmapToByteArray(inputStream?.use(BitmapFactory::decodeStream))
//            }else{
//                null
//            }
//
//
//
//
//            // Add error handling and add the contact to the list
//            if (contactId != null) {
//                contacts.add(Contact(name, lastName = lastName, phoneNumber, email, contactId,photoByteArray))
//            }
//        }
//    }
//    cursor?.close()
//    val contactEmails =  getContactEmails(contentResolver)
//    contacts.forEach {
//        contactEmails[it.contactId]?.let { emails ->
//            it.email = emails[0]
//        }
//    }
//
//    contact(contentResolver)
//
//    return contacts
//}
// private fun getContactPhoto(context: Context, contactId: Long): Bitmap? {
//    val photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId)
//    val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.contentResolver, photoUri)
//    return inputStream?.use(BitmapFactory::decodeStream)
//}

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


fun saveContact(context : Context, contact: Contact) {
    val values = ContentValues()
//    values.put(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
//    values.put(ContactsContract.RawContacts.ACCOUNT_NAME, null)


    val rawContactUri = context.contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, values)
    val rawContactId = rawContactUri?.lastPathSegment?.toLongOrNull()

    rawContactId?.let { id ->
        // Insert Name
        values.clear()
        values.put(ContactsContract.Data.RAW_CONTACT_ID, id)
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contact.name)
        values.put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, contact.lastName)
        context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, values)

        // Insert Phone Number
        values.clear()
        values.put(ContactsContract.Data.RAW_CONTACT_ID, id)
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.phoneNumber)
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
        context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, values)

        // Insert Email
        values.clear()
        values.put(ContactsContract.Data.RAW_CONTACT_ID, id)
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
        values.put(ContactsContract.CommonDataKinds.Email.DATA, contact.email)
        values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
        context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, values)

        // Insert Photo
        contact?.photo?.let { byteArray ->
            values.clear()
            values.put(ContactsContract.Data.RAW_CONTACT_ID, id)
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
            values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, byteArray)
            context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, values)
        }
    }
}


fun editContact(context: Context,contact: Contact) {

        // Update Name
        val nameValues = ContentValues().apply {
            put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contact.name)
            put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, contact.lastName)
        }
        context.contentResolver.update(
            ContactsContract.Data.CONTENT_URI,
            nameValues,
            "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
            arrayOf(contact.contactId.toString(), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
        )

        // Update Phone Number
        val phoneValues = ContentValues().apply {
            put(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.phoneNumber)
        }
        context.contentResolver.update(
            ContactsContract.Data.CONTENT_URI,
            phoneValues,
            "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
            arrayOf(contact.contactId.toString(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        )

        // Update Email
        val emailValues = ContentValues().apply {
            put(ContactsContract.CommonDataKinds.Email.ADDRESS, contact.email)
        }
        context.contentResolver.update(
            ContactsContract.Data.CONTENT_URI,
            emailValues,
            "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
            arrayOf(contact.contactId.toString(), ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
        )

    val editedPhotoByteArray = contact.photo

    // Update Photo
    if (editedPhotoByteArray != null) {
        val photoValues = ContentValues().apply {
            put(ContactsContract.CommonDataKinds.Photo.PHOTO, editedPhotoByteArray)
        }
        context.contentResolver.update(
            ContactsContract.Data.CONTENT_URI,
            photoValues,
            "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
            arrayOf(
                contact.contactId.toString(),
                ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
            )
        )
    }

}

fun bitmapToByteArray(bitmap: Bitmap?, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 100): ByteArray? {
    if(bitmap == null)
        return null
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(format, quality, outputStream)
    return outputStream.toByteArray()
}

private fun getContactEmails(contentResolver: ContentResolver): HashMap<String, ArrayList<String>> {
    val contactsEmailMap = HashMap<String, ArrayList<String>>()
    val emailCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
        null,
        null,
        null,
        null)
    if (emailCursor != null && emailCursor.count > 0) {
        val contactIdIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
        val emailIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
        while (emailCursor.moveToNext()) {
            val contactId = emailCursor.getString(contactIdIndex)
            val email = emailCursor.getString(emailIndex)
            //check if the map contains key or not, if not then create a new array list with email
            if (contactsEmailMap.containsKey(contactId)) {
                contactsEmailMap[contactId]?.add(email)
            } else {
                contactsEmailMap[contactId] = arrayListOf(email)
            }
        }
        //contact contains all the emails of a particular contact
        emailCursor.close()
    }
    return contactsEmailMap
}

fun uriToByteArray(contentResolver: ContentResolver, uri: Uri?): ByteArray? {
    if(uri == null)
        return null
    var inputStream: InputStream? = null
    try {
        inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        inputStream?.close()
    }
    return null
}

class UserContactDetail {
    var name: String? = null
    var emailId: List<String> = ArrayList()
    var phoneNumber: List<String> = ArrayList()
}

fun contact(contentResolver: ContentResolver) : List<Contact> {
    val userContactDetailLinkedHashMap: LinkedHashMap<Int, Contact?> = LinkedHashMap()
    try {
        val cr: ContentResolver = contentResolver
        val cur: Cursor? = cr.query(
            ContactsContract.Data.CONTENT_URI,
            PROJECTION,
            selection,
            selectionArgs,
            ORDER
        )
        if (cur != null && cur.getCount()>0 && cur.moveToFirst()) {
            do {
                val hasPhone: Int =
                    cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                val contactId: Int =
                    cur.getInt(cur.getColumnIndex(ContactsContract.Data.CONTACT_ID))
                val name: String =
                    cur.getString(cur.getColumnIndex(ContactsContract.Data.DISPLAY_NAME))
                val emailOrMobile: String? =
                    cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA1))
                val photoByteArray = if(contactId!=null){
                    val photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId.toLong())
                    val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, photoUri)
                    bitmapToByteArray(inputStream?.use(BitmapFactory::decodeStream))
                }else{
                    null
                }
                if (hasPhone > 0 && emailOrMobile != null) {
                    var contactDetail: Contact?
                    if (!userContactDetailLinkedHashMap.containsKey(contactId)) {
                        contactDetail = Contact()
                        if (!Utils.isEmailValid(emailOrMobile)) {
                            contactDetail.phoneNumber = (Collections.singletonList(emailOrMobile)).getOrNull(0)
                        } else {
                            contactDetail.email = Collections.singletonList(emailOrMobile).getOrNull(0)
                        }
                        contactDetail.name = (name)
                        userContactDetailLinkedHashMap[contactId] = contactDetail
                    } else {
                        contactDetail = userContactDetailLinkedHashMap[contactId]
                        if (contactDetail == null) continue
                        if (!Utils.isEmailValid(emailOrMobile)) {
                            contactDetail.phoneNumber = (Collections.singletonList(emailOrMobile)).getOrNull(0)
                        } else {
                            contactDetail.email = (Collections.singletonList(emailOrMobile)).getOrNull(0)
                        }
                        contactDetail.name = (name)
                        userContactDetailLinkedHashMap[contactId] = contactDetail
                    }
                    contactDetail.photo = photoByteArray
                    contactDetail.contactId = contactId.toString()
                }
            } while (cur.moveToNext())
        }
        if (cur != null) {
            cur.close()
        }
      return  userContactDetailLinkedHashMap.mapNotNull { it.value }
    } catch (e: Exception) {
        return listOf<Contact>()
//        Crashlytics.getInstance().core.logException(e)
    }
}


