package com.santiifm.milou.data.service

import com.santiifm.milou.data.model.DownloadItemModel
import com.santiifm.milou.data.model.DownloadStatus
import com.santiifm.milou.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadProgressTracker @Inject constructor() {
    
    private val _downloads = MutableStateFlow<List<DownloadItemModel>>(emptyList())
    val downloads: StateFlow<List<DownloadItemModel>> = _downloads
    
    fun updateDownloadStatus(fileName: String, status: DownloadStatus) {
        _downloads.value = _downloads.value.map { item ->
            if (item.fileName == fileName) {
                item.copy(status = status)
            } else {
                item
            }
        }
    }
    
    fun updateDownloadProgress(fileName: String, progress: Float, speed: Float, downloadedBytes: Long) {
        _downloads.value = _downloads.value.map { item ->
            if (item.fileName == fileName) {
                item.copy(progress = progress, downloadSpeed = speed, downloadedBytes = downloadedBytes)
            } else {
                item
            }
        }
    }
    
    fun addDownload(downloadItem: DownloadItemModel) {
        _downloads.value = _downloads.value + downloadItem
    }
    
    fun removeDownload(fileName: String) {
        _downloads.value = _downloads.value.filter { it.fileName != fileName }
    }
    
    fun getDownloads(): List<DownloadItemModel> {
        return _downloads.value
    }
    
    fun canRetryDownload(fileName: String): Boolean {
        return _downloads.value.any { 
            it.fileName == fileName && it.status == DownloadStatus.FAILED 
        }
    }
    
    fun hasActiveDownloads(): Boolean {
        return _downloads.value.any { 
            it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.UNZIPPING 
        }
    }
    
    fun calculateProgressPercentage(progress: Float): Int {
        return (progress * 100).toInt().coerceIn(0, 100)
    }
    
    fun shouldUpdateProgress(progress: Float, lastUpdateTime: Long, currentTime: Long): Boolean {
        return progress >= Constants.PROGRESS_COMPLETE ||
               (currentTime - lastUpdateTime) > Constants.PROGRESS_UPDATE_INTERVAL_MS
    }
}
