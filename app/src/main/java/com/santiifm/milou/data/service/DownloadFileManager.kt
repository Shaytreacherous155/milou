package com.santiifm.milou.data.service

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.santiifm.milou.data.local.entity.DownloadableFileEntity
import com.santiifm.milou.data.model.DownloadItemModel
import com.santiifm.milou.data.model.DownloadStatus
import com.santiifm.milou.data.repository.ConsoleRepository
import com.santiifm.milou.data.repository.SettingsRepository
import com.santiifm.milou.util.ConsoleFormatter
import com.santiifm.milou.util.StorageHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadFileManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val consoleRepository: ConsoleRepository
) {
    
    suspend fun createDownloadItem(file: DownloadableFileEntity): DownloadItemModel {
        val consoles = consoleRepository.getAllConsoles().first()
        val console = consoles.find { it.id == file.consoleId }
        
        return DownloadItemModel(
            name = file.name,
            fileName = file.fileName,
            downloadSpeed = 0f,
            progress = 0f,
            status = DownloadStatus.DOWNLOADING,
            downloadedBytes = 0L,
            fileSize = file.fileSize
        )
    }
    
    fun createDocumentFile(
        file: DownloadableFileEntity,
        downloadDirectoryUri: String,
        subPath: String
    ): DocumentFile? {
        return StorageHelper.createFile(
            context = context,
            uriString = downloadDirectoryUri,
            subPath = subPath,
            fileName = file.fileName,
            mimeType = "application/octet-stream"
        )
    }
    
    fun getOutputStream(documentFile: DocumentFile): java.io.OutputStream? {
        return StorageHelper.getOutputStream(context, documentFile)
    }
    
    fun deleteFile(documentFile: DocumentFile): Boolean {
        return try {
            StorageHelper.deleteFile(documentFile)
            true
        } catch (_: Exception) {
            false
        }
    }
    
    suspend fun getDownloadDirectoryUri(): Uri {
        return settingsRepository.downloadDirectory.first().toUri()
    }
    
    suspend fun getSubPath(file: DownloadableFileEntity): String {
        val separateByConsole = settingsRepository.separateByConsole.first()
        
        if (!separateByConsole) return ""
        
        val consoles = consoleRepository.getAllConsoles().first()
        val console = consoles.find { it.id == file.consoleId }
        return if (console != null) {
            ConsoleFormatter.getConsoleDisplayName(console.id)
        } else {
            "Unknown"
        }
    }
}
