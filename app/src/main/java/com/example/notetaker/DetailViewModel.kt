package com.example.notetaker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecordRepository

    private val _record = MutableLiveData<Record?>()
    val record: LiveData<Record?> = _record

    private val _deleteStatus = MutableLiveData<Boolean>()
    val deleteStatus: LiveData<Boolean> = _deleteStatus

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    init {
        val recordDao = AppDatabase.getDatabase(application).recordDao()
        repository = RecordRepository(recordDao)
    }

    fun loadRecord(recordId: Long) {
        viewModelScope.launch {
            try {
                _record.value = repository.getRecordById(recordId)
            } catch (e: Exception) {
                _toastMessage.value = "Error loading record: ${e.message}"
            }
        }
    }

    fun deleteRecord() {
        _record.value?.let { currentRecord ->
            viewModelScope.launch {
                try {
                    repository.delete(currentRecord)
                    _deleteStatus.value = true
                    _toastMessage.value = "Record deleted successfully"
                } catch (e: Exception) {
                    _deleteStatus.value = false
                    _toastMessage.value = "Error deleting record: ${e.message}"
                }
            }
        }
    }

    fun onDeleteComplete() {
        _deleteStatus.value = false
    }
}