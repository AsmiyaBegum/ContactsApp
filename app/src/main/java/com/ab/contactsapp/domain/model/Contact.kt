package com.ab.contactsapp.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import kotlinx.parcelize.Parcelize


data class ContactInfoResponse(
    val results: List<ContactInfo>,
    val info: Info
)


@Parcelize
@Entity
data class Contact(
    var name: String? = "",
    var lastName: String? = "",
    var phoneNumber: String? = "",
    var email: String? = "",
    @PrimaryKey var contactId: String = UUID.randomUUID().toString(),
    var photo: ByteArray? = null,
    var isRandomContact: Boolean = false,
    var photoUrl: String? = null
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
    val email: String? = "",
    val facebook: String? = "",
    val twitter: String? = ""
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

fun ContactInfo.toContactEntity(): Contact {
    return Contact(
        name = name.first + name.last,
        email = email,
        phoneNumber = phone,
        photoUrl = picture.large
    )
}
