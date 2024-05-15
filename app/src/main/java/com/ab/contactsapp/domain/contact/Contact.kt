package com.ab.contactsapp.domain.contact

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.versionedparcelable.ParcelField
import java.util.UUID
import kotlinx.parcelize.Parcelize



data class ContactInfoResponse(
    val contacts: List<ContactInfo>,
    val info: Info
)


@Parcelize
@Entity
data class  Contact(
    var name: String? = "",
    val lastName : String? = "",
    var phoneNumber: String? = "",
    var email: String? = "",
    @PrimaryKey var contactId: String = UUID.randomUUID().toString(),
    var photo: ByteArray? = null,
    var isRandomContact : Boolean = false
) : Parcelable


data class ContactInfo(
    val name: ContactName,
    val email: String,
    val phone: String,
    val cell: String,
    val id: ContactId,
    val picture: UserPicture
)

data class ContactName(
    val title: String,
    val first: String,
    val last: String
)

data class ContactId(
    val name: String?,
    val value: String?
)

data class UserPicture(
    val large: String,
    val medium: String,
    val thumbnail: String
)

data class Info(
    val seed: String?,
    val results: Int,
    val page: Int,
    val version: String
)


data class SocialMedia(
    val email : String? = "",
    val facebook : String? = "",
    val twitter : String? = ""
)

data class CallLogEntry(
    val id: Long,
    val number: String,
    val date: Long,
    val duration: Long,
    val type: String
)

data class CallLogGroup(
    val date: Long, // Date in milliseconds
    val callLogs: List<CallLogEntry>
)


fun generateMockContactInfoList(): List<ContactInfo> {
    val contacts = mutableListOf<ContactInfo>()

    // Iterate over the alphabets A-Z
    ('A'..'Z').forEach { letter ->
        val contactName = ContactName(
            title = "Mr",
            first = "$letter John",
            last = "Doe"
        )
        val contactId = ContactId(name = null, value = null)
        val userPicture = UserPicture(
            large = "https://randomuser.me/api/portraits/men/1.jpg",
            medium = "https://randomuser.me/api/portraits/med/men/1.jpg",
            thumbnail = "https://randomuser.me/api/portraits/thumb/men/1.jpg"
        )
        val contact = ContactInfo(
            name = contactName,
            email = "${letter.toLowerCase()}john@example.com",
            phone = "123456789",
            cell = "987654321",
            id = contactId,
            picture = userPicture
        )
        contacts.add(contact)
        contacts.add(contact)
    }

    return contacts
}

fun generateMockContacts(): List<Contact> {
    val mockContacts = mutableListOf<Contact>()
    for (i in 1..10) {
        mockContacts.add(
            Contact(
                name = "Contact $i",
                phoneNumber = "(123) 456-789$i",
                email = "contact$i@example.com",
                contactId = i.toString(),
                photo = null // You can add mock photo data here if needed
            )
        )
    }
    return mockContacts
}

// Usage
val mockContacts = generateMockContacts()

