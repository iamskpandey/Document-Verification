package com.saileshkumarpandey.scholarverifycollege.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "document")
data class Document(
    @PrimaryKey(autoGenerate = true) val did: Int = 0,
    val title: String,
    val imagePath: String,
    var status: String,
    val sid: Int,
    val fid: Int
)
