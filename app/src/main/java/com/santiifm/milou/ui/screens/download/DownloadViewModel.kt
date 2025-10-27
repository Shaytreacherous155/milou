package com.santiifm.milou.ui.screens.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiifm.milou.data.model.DownloadItemModel
import com.santiifm.milou.data.repository.DownloadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val repository: DownloadRepository
) : ViewModel() {

    val downloads: StateFlow<List<DownloadItemModel>> = repository.downloads
    
    private val _showDeleteConfirmation = MutableStateFlow<String?>(null)
    val showDeleteConfirmation: StateFlow<String?> = _showDeleteConfirmation.asStateFlow()

    fun cancelDownload(fileName: String) {
        viewModelScope.launch {
            repository.cancelDownload(fileName)
        }
    }

    fun retryDownload(fileName: String) {
        viewModelScope.launch { 
            repository.retryDownload(fileName) 
        }
    }

    fun deleteDownload(fileName: String, deleteFile: Boolean = false) {
        viewModelScope.launch { 
            repository.deleteDownload(fileName, deleteFile) 
        }
    }
    
    fun deleteDownloadWithConfirmation(fileName: String, isCompleted: Boolean) {
        if (isCompleted) {
            _showDeleteConfirmation.value = fileName
        } else {
            deleteDownload(fileName, deleteFile = false)
        }
    }
    
    fun confirmDeleteKeepFile(fileName: String) {
        _showDeleteConfirmation.value = null
        deleteDownload(fileName, deleteFile = false)
    }
    
    fun confirmDeleteRemoveFile(fileName: String) {
        _showDeleteConfirmation.value = null
        deleteDownload(fileName, deleteFile = true)
    }
    
    fun cancelDeleteConfirmation() {
        _showDeleteConfirmation.value = null
    }
}
