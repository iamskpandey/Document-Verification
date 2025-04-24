package com.saileshkumarpandey.scholarverifycollege.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Student::class, Document::class, Faculty::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun documentDao(): DocumentDao
    abstract fun facultyDao(): FacultyDao
}
