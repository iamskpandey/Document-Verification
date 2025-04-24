package com.saileshkumarpandey.scholarverifycollege.database

import androidx.room.*

@Dao
interface DocumentDao {
    @Insert
    suspend fun insert(document: Document): Long

    @Update
    suspend fun update(document: Document)

    @Delete
    suspend fun delete(document: Document)

    @Query("SELECT * FROM document WHERE did = :id")
    suspend fun getDocumentById(id: Int): Document?

    @Query("SELECT * FROM document")
    suspend fun getAllDocuments(): List<Document>

    @Query("SELECT fid FROM faculty ORDER BY new_request_count ASC LIMIT 1")
    suspend fun getFacultyWithLeastRequests(): Int

    @Query("SELECT * FROM document WHERE sid = :sid")
    suspend fun getDocumentsBySid(sid: Int): List<Document>

    @Query("SELECT * FROM document WHERE fid = :fid")
    suspend fun getDocumentsByFid(fid: Int): List<Document>
}
