package com.example.contactbd.databas

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(contacts: MutableList<Contact>)

    @Query("select * FROM contact ")
    fun getContacts(): MutableList<Contact>
}