package com.saileshkumarpandey.scholarverifycollege.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "faculty")
data class Faculty(
    @PrimaryKey(autoGenerate = true) val fid: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    var new_request_count: Int,
    var approved_count: Int,
    var rejected_count: Int,
    val pending_count: Int
)
