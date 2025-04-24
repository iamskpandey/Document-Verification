package com.saileshkumarpandey.scholarverifycollege.database

import androidx.room.*

@Dao
interface StudentDao {
    @Insert
    suspend fun insert(student: Student): Long

    @Update
    suspend fun update(student: Student)

    @Delete
    suspend fun delete(student: Student)

    @Query("SELECT * FROM student WHERE sid = :id")
    suspend fun getStudentById(id: Int): Student?

    @Query("SELECT * FROM student")
    suspend fun getAllStudents(): List<Student>

    @Query("SELECT * FROM student WHERE email = :email AND password = :password")
    suspend fun getStudentByEmailAndPassword(email: String, password: String): Student?

    @Query("SELECT sid FROM student WHERE email = :email")
    suspend fun getStudentIdByEmail(email: String): Int?
}
