package com.example.notetaker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecordRepository

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    private val _records: LiveData<List<Record>>
    val records: LiveData<List<Record>>

    private val _sortOrder = MutableLiveData<SortOrder>()
    val sortOrder: LiveData<SortOrder> = _sortOrder

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    init {
        val recordDao = AppDatabase.getDatabase(application).recordDao()
        repository = RecordRepository(recordDao)
        _searchQuery.value = ""
        _sortOrder.value = SortOrder.DATE_DESC

        _records = _searchQuery.switchMap { query ->
            if (query.isBlank()) {
                repository.allRecords
            } else {
                repository.searchRecords(query)
            }
        }
        records = _sortOrder.switchMap { order ->
            when (order) {
                SortOrder.DATE_ASC -> repository.getRecordsSortedByDateAsc()
                SortOrder.DATE_DESC -> repository.getRecordsSortedByDateDesc()
                SortOrder.NAME_ASC -> repository.getRecordsSortedByNameAsc()
                SortOrder.NAME_DESC -> repository.getRecordsSortedByNameDesc()
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun deleteRecord(record: Record) {
        viewModelScope.launch {
            try {
                repository.delete(record)
                _toastMessage.value = "Record deleted successfully"
            } catch (e: Exception) {
                _toastMessage.value = "Error deleting record: ${e.message}"
            }
        }
    }

    enum class SortOrder {
        DATE_ASC, DATE_DESC, NAME_ASC, NAME_DESC
    }
}