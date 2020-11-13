package com.example.contactbd.databas

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
 data class Contact(
    @PrimaryKey
    @ColumnInfo(name = "id")val cId: Int,
    @ColumnInfo(name = "Name") val name: String,
    @ColumnInfo(name = "Phone") val phone: String

)