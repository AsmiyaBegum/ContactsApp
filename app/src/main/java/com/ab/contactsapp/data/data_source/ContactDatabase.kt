package com.ab.contactsapp.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ab.contactsapp.domain.model.Contact


@Database(entities = [Contact::class], version = 1, exportSchema = true)
abstract class ContactDatabase : RoomDatabase(){

    abstract val contactsDao: ContactsDao

    companion object {
        const val DATABASE_NAME = "contacts_db"
    }

}