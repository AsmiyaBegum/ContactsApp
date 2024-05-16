package com.ab.contactsapp.data.data_source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ab.contactsapp.domain.model.Contact

@Dao
interface ContactsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContactList(contacts : List<Contact> )

    @Query("DELETE FROM contact")
    suspend fun deleteAllContact()

}