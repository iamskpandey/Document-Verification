package com.saileshkumarpandey.scholarverifycollege.database

import android.content.Context
import androidx.room.Room

object DatabaseInstance {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "scholar_verify_college_db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
