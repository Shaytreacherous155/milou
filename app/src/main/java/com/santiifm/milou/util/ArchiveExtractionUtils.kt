package com.santiifm.milou.util

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.santiifm.milou.util.Constants.EXTRACTION_BUFFER_SIZE
import com.santiifm.milou.util.Constants.MAX_ARCHIVE_ENTRIES
import java.io.InputStream
import java.io.OutputStream

object ArchiveExtractionUtils {
    
    private const val TAG = "ArchiveExtractionUtils"
    
    fun validateArchiveFile(context: Context, archiveUri: Uri): DocumentFile? {
        val archiveFile = DocumentFile.fromSingleUri(context, archiveUri)
        if (archiveFile == null || !archiveFile.exists()) {
            Log.e(TAG, "Archive file not found: $archiveUri")
            return null
        }
        return archiveFile
    }
    
    fun getFileExtension(fileName: String): String {
        return fileName.substringAfterLast(".", "")
    }
    
    fun sanitizeFileName(fileName: String): String {
        return fileName.replace(Regex("[<>:\"/\\\\|?*]"), "_")
    }
    
    fun createDestinationDirectory(
        context: Context, 
        destinationUri: Uri, 
        subPath: String
    ): DocumentFile? {
        val destinationDir = DocumentFile.fromTreeUri(context, destinationUri)
            ?: return null
            
        if (subPath.isEmpty()) return destinationDir
        
        val pathParts = subPath.split("/").filter { it.isNotEmpty() }
        var currentDir = destinationDir
        
        for (part in pathParts) {
            val existingDir = currentDir.findFile(part)
            currentDir = if (existingDir != null && existingDir.isDirectory) {
                existingDir
            } else {
                currentDir.createDirectory(part) ?: return null
            }
        }
        
        return currentDir
    }
    
    fun copyStream(
        inputStream: InputStream, 
        outputStream: OutputStream, 
        bufferSize: Int = EXTRACTION_BUFFER_SIZE
    ): Long {
        val buffer = ByteArray(bufferSize)
        var totalBytes = 0L
        var bytesRead: Int
        
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
            totalBytes += bytesRead
        }
        
        return totalBytes
    }
    
    fun validateEntryCount(entryCount: Int): Boolean {
        if (entryCount > MAX_ARCHIVE_ENTRIES) {
            Log.w(TAG, "Archive has too many entries: $entryCount (max: $MAX_ARCHIVE_ENTRIES)")
            return false
        }
        return true
    }
    
    fun calculateProgress(current: Int, total: Int): Float {
        return if (total > 0) {
            (current.toFloat() / total.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    }
    
    fun logProgress(extractedCount: Int, totalCount: Int, fileName: String) {
        if (extractedCount % 10 == 0 || extractedCount == totalCount) {
            Log.d(TAG, "Extracted $extractedCount/$totalCount files. Current: $fileName")
        }
    }
    
    fun prepareExtractionDestination(
        context: Context,
        destinationUri: Uri,
        subPath: String
    ): Uri {
        return if (subPath.isNotEmpty()) {
            val consoleDir = createDestinationDirectory(context, destinationUri, subPath)
            if (consoleDir != null) {
                Log.d(TAG, "Created console directory for extraction: $subPath")
                consoleDir.uri
            } else {
                Log.w(TAG, "Failed to create console directory: $subPath, using root destination")
                destinationUri
            }
        } else {
            Log.d(TAG, "Extracting to root destination (no console separation)")
            destinationUri
        }
    }
}
