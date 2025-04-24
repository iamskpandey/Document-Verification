package com.saileshkumarpandey.scholarverifycollege.database

import androidx.room.*

@Dao
interface FacultyDao {
    @Insert
    suspend fun insert(faculty: Faculty): Long

    @Update
    suspend fun update(faculty: Faculty)

    @Delete
    suspend fun delete(faculty: Faculty)

    @Query("SELECT * FROM faculty WHERE fid = :id")
    suspend fun getFacultyById(id: Int): Faculty?

    @Query("SELECT * FROM faculty")
    suspend fun getAllFaculty(): List<Faculty>

    @Query("SELECT * FROM faculty WHERE email = :email AND password = :password")
    suspend fun getFacultyByEmailAndPassword(email: String, password: String): Faculty?

    @Query("SELECT fid FROM faculty WHERE email = :email")
    suspend fun getFacultyIdByEmail(email: String): Int?
}
