package com.saileshkumarpandey.scholarverifycollege.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student")
data class Student(
    @PrimaryKey(autoGenerate = true) val sid: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val cgpa: Double,
    val no_of_courses_studied: Int,
    val backlog: Int,
    val number_of_remained: Int,
    val scheduled_count: Int,
    var approved_count: Int,
    var rejected_count: Int,
    var pending_count: Int
)
