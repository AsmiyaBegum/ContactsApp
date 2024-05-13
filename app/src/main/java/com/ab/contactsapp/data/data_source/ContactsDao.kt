package com.ab.contactsapp.data.data_source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ab.contactsapp.domain.contact.Contact
import com.ab.contactsapp.domain.contact.ContactInfo

@Dao
interface ContactsDao {

    @Query("SELECT * FROM contact")
    fun getContact(): List<Contact>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContactList(contacts : List<Contact> )

    @Query("DELETE FROM contact")
    suspend fun deleteAllContatct()

}