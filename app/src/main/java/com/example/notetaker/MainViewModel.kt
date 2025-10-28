package com.example.notetaker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecordRepository
    val allRecords: LiveData<List<Record>>

    private val _currentRecord = MutableLiveData<Record?>()
    val currentRecord: LiveData<Record?> = _currentRecord

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> = _saveStatus

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val _searchResults = MutableLiveData<List<Record>>()
    val searchResults: LiveData<List<Record>> = _searchResults

    init {
        val recordDao = AppDatabase.getDatabase(application).recordDao()
        repository = RecordRepository(recordDao)
        allRecords = repository.allRecords
    }

    fun saveRecord(name: String, number: String, date: String, action: String, imagePaths: String) {
        if (name.isBlank() || number.isBlank() || date.isBlank() || action.isBlank()) {
            _toastMessage.value = "Please fill in all fields"
            return
        }

        val record = _currentRecord.value?.copy(
            name = name,
            number = number,
            date = date,
            action = action,
            imagePaths = imagePaths
        ) ?: Record(
            name = name,
            number = number,
            date = date,
            action = action,
            imagePaths = imagePaths
        )

        viewModelScope.launch {
            try {
                if (_currentRecord.value != null) {
                    repository.update(record)
                    _toastMessage.value = "Record updated successfully"
                } else {
                    repository.insert(record)
                    _toastMessage.value = "Record saved successfully"
                }
                _saveStatus.value = true
            } catch (e: Exception) {
                _toastMessage.value = "Error saving record: ${e.message}"
                _saveStatus.value = false
            }
        }
    }

    fun loadRecord(recordId: Long) {
        viewModelScope.launch {
            try {
                _currentRecord.value = repository.getRecordById(recordId)
            } catch (e: Exception) {
                _toastMessage.value = "Error loading record: ${e.message}"
            }
        }
    }

    fun clearCurrentRecord() {
        _currentRecord.value = null
        _saveStatus.value = false
    }

    fun onSaveComplete() {
        _saveStatus.value = false
    }

    fun searchRecords(query: String) {
        viewModelScope.launch {
            try {
                allRecords.value?.let {
                    val filteredList = if (query.isEmpty()) {
                        emptyList()
                    } else {
                        it.filter { record ->
                            record.name.contains(query, ignoreCase = true) ||
                                    record.number.contains(query, ignoreCase = true) ||
                                    record.action.contains(query, ignoreCase = true)
                        }
                    }
                    _searchResults.postValue(filteredList)
                }
            } catch (e: Exception) {
                _toastMessage.value = "Error searching records: ${e.message}"
            }
        }
    }
}