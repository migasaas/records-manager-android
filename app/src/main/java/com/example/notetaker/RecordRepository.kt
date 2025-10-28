package com.example.notetaker

import androidx.lifecycle.LiveData

class RecordRepository(private val recordDao: RecordDao) {

    val allRecords: LiveData<List<Record>> = recordDao.getAllRecords()

    suspend fun insert(record: Record): Long {
        return recordDao.insertRecord(record)
    }

    suspend fun update(record: Record) {
        recordDao.updateRecord(record)
    }

    suspend fun delete(record: Record) {
        recordDao.deleteRecord(record)
    }

    suspend fun getRecordById(id: Long): Record? {
        return recordDao.getRecordById(id)
    }

    fun searchRecords(query: String): LiveData<List<Record>> {
        return recordDao.searchRecords("%$query%")
    }

    fun getRecordsSortedByDateAsc(): LiveData<List<Record>> {
        return recordDao.getRecordsSortedByDateAsc()
    }

    fun getRecordsSortedByDateDesc(): LiveData<List<Record>> {
        return recordDao.getRecordsSortedByDateDesc()
    }

    fun getRecordsSortedByNameAsc(): LiveData<List<Record>> {
        return recordDao.getRecordsSortedByNameAsc()
    }

    fun getRecordsSortedByNameDesc(): LiveData<List<Record>> {
        return recordDao.getRecordsSortedByNameDesc()
    }
}