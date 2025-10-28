package com.example.notetaker

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: Record): Long

    @Update
    suspend fun updateRecord(record: Record)

    @Delete
    suspend fun deleteRecord(record: Record)

    @Query("SELECT * FROM records ORDER BY id DESC")
    fun getAllRecords(): LiveData<List<Record>>

    @Query("SELECT * FROM records WHERE id = :id")
    suspend fun getRecordById(id: Long): Record?

    @Query("SELECT * FROM records WHERE name LIKE :query OR number LIKE :query ORDER BY id DESC")
    fun searchRecords(query: String): LiveData<List<Record>>

    @Query("SELECT * FROM records ORDER BY date ASC")
    fun getRecordsSortedByDateAsc(): LiveData<List<Record>>

    @Query("SELECT * FROM records ORDER BY date DESC")
    fun getRecordsSortedByDateDesc(): LiveData<List<Record>>

    @Query("SELECT * FROM records ORDER BY name ASC")
    fun getRecordsSortedByNameAsc(): LiveData<List<Record>>

    @Query("SELECT * FROM records ORDER BY name DESC")
    fun getRecordsSortedByNameDesc(): LiveData<List<Record>>
}