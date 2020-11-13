package com.example.contactbd.databas

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Contact::class],version = 1 )
 abstract class ContactDataBase : RoomDatabase() {
    abstract fun contactDAO(): ContactDao

    companion object {
        var INSTANCE: ContactDataBase? = null

        fun getContactDataBase(context: Context): ContactDataBase? {
            if (INSTANCE == null){
                synchronized(ContactDataBase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            ContactDataBase::class.java, "myContactsDb")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}