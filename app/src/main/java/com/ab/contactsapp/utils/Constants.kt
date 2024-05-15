package com.ab.contactsapp.utils

object Constants {
    const val SOCIAL_MEDIA = "Social Media"
    const val RECENTS = "Recents"

    const val RANDOM_CONTACTS = "Random"
    const val PHONE_CONTACTS = "Phone"

    const val RESULT_SIZE = 25
     val CONTACTS_LIST = listOf(PHONE_CONTACTS, RANDOM_CONTACTS)
    val A_Z_LIST = ('A'..'Z').toList() + '#'

    const val DELETE = "Delete"
    const val MARK_AS_FAV = "Mark As Favorite"
    const val CALL_LOGS = "Call History"
    val OPTION_MENU_LIST = listOf(CALL_LOGS, MARK_AS_FAV, DELETE)

}