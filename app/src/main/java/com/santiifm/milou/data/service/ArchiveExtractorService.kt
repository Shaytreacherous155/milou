package com.santiifm.milou.data.service

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.santiifm.milou.util.ArchiveUtils
import com.santiifm.milou.util.ArchiveExtractionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.zip.ZipInputStream
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveExtractorService @Inject constructor() {
    
    suspend fun extractArchive(
        context: Context,
        archiveUri: Uri,
        destinationUri: Uri,
        subPath: String = "",
        onProgress: (Float) -> Unit = {}
    ): List<String> = withContext(Dispatchers.IO) {
        try {
            Log.d("ArchiveExtractorService", "Starting extraction of $archiveUri to $destinationUri")
            
            val archiveFile = ArchiveExtractionUtils.validateArchiveFile(context, archiveUri)
                ?: return@withContext emptyList()
            
            val fileName = archiveFile.name ?: "unknown"
            val fileExtension = ArchiveExtractionUtils.getFileExtension(fileName)
            
            if (!ArchiveUtils.isExtractable(".$fileExtension")) {
                Log.w("ArchiveExtractorService", "File extension not supported for extraction: $fileExtension")
                return@withContext emptyList()
            }
            
            return@withContext when (fileExtension.lowercase()) {
                "zip" -> extractZipFile(context, archiveUri, destinationUri, subPath, onProgress)
                "7z" -> extract7zFile(context, archiveUri, destinationUri, subPath, onProgress)
                else -> {
                    Log.w("ArchiveExtractorService", "Archive type not yet implemented: $fileExtension")
                    emptyList()
                }
            }
            
        } catch (e: Exception) {
            Log.e("ArchiveExtractorService", "Error during extraction: ${e.message}", e)
            emptyList()
        }
    }
    
    private suspend fun extractZipFile(
        context: Context,
        archiveUri: Uri,
        destinationUri: Uri,
        subPath: String,
        onProgress: (Float) -> Unit
    ): List<String> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(archiveUri)
                ?: return@withContext emptyList()
            
            val zipInputStream = ZipInputStream(inputStream)
            var entry = zipInputStream.nextEntry
            var extractedCount = 0
            var totalEntries = 0
            val extractedFiles = mutableListOf<String>()
            
            val tempStream = context.contentResolver.openInputStream(archiveUri)
            val tempZipStream = ZipInputStream(tempStream)
            while (tempZipStream.nextEntry != null) {
                totalEntries++
            }
            tempZipStream.close()
            tempStream?.close()
            
            if (!ArchiveExtractionUtils.validateEntryCount(totalEntries)) {
                Log.e("ArchiveExtractorService", "Archive has too many entries: $totalEntries")
                return@withContext emptyList()
            }
            
            Log.d("ArchiveExtractorService", "Total entries to extract: $totalEntries")
            
            val finalDestinationUri = ArchiveExtractionUtils.prepareExtractionDestination(
                context, 
                destinationUri, 
                subPath
            )
            
            while (entry != null) {
                if (!entry.isDirectory) {
                    val fileName = entry.name
                    val sanitizedFileName = ArchiveExtractionUtils.sanitizeFileName(fileName)
                    
                    val finalFileName = if (sanitizedFileName.contains("/")) {
                        val parts = sanitizedFileName.split("/")
                        if (parts.size > 1) {
                            parts.drop(1).joinToString("/")
                        } else {
                            sanitizedFileName
                        }
                    } else {
                        sanitizedFileName
                    }
                    
                    val outputUri = try {
                        val destinationDir = DocumentFile.fromTreeUri(context, finalDestinationUri)
                        if (destinationDir != null) {
                            val outputFile = destinationDir.createFile("application/octet-stream", finalFileName)
                            outputFile?.uri
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("ArchiveExtractorService", "Error creating output file: ${e.message}")
                        null
                    }
                    
                    if (outputUri != null) {
                        val outputStream = context.contentResolver.openOutputStream(outputUri)
                        if (outputStream != null) {
                            ArchiveExtractionUtils.copyStream(zipInputStream, outputStream)
                            outputStream.close()
                            extractedCount++
                            extractedFiles.add(finalFileName)
                            ArchiveExtractionUtils.logProgress(extractedCount, totalEntries, finalFileName)
                            onProgress(ArchiveExtractionUtils.calculateProgress(extractedCount, totalEntries))
                        }
                    }
                }
                entry = zipInputStream.nextEntry
            }
            
            zipInputStream.close()
            inputStream.close()
            
            Log.d("ArchiveExtractorService", "Successfully extracted $extractedCount files")
            extractedFiles
            
        } catch (e: Exception) {
            Log.e("ArchiveExtractorService", "Error extracting ZIP: ${e.message}", e)
            emptyList()
        }
    }
    
    private suspend fun extract7zFile(
        context: Context,
        archiveUri: Uri,
        destinationUri: Uri,
        subPath: String,
        onProgress: (Float) -> Unit
    ): List<String> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(archiveUri)
                ?: return@withContext emptyList()
            
            val tempFile = File.createTempFile("temp_7z", ".7z")
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            
            val sevenZFile = SevenZFile(tempFile)
            var entry: SevenZArchiveEntry? = sevenZFile.nextEntry
            var extractedCount = 0
            var totalEntries = 0
            val extractedFiles = mutableListOf<String>()
            
            val tempSevenZFile = SevenZFile(tempFile)
            while (tempSevenZFile.nextEntry != null) {
                totalEntries++
            }
            tempSevenZFile.close()
            
            if (!ArchiveExtractionUtils.validateEntryCount(totalEntries)) {
                Log.e("ArchiveExtractorService", "Archive has too many entries: $totalEntries")
                tempFile.delete()
                return@withContext emptyList()
            }
            
            Log.d("ArchiveExtractorService", "Total entries to extract: $totalEntries")
            
            val finalDestinationUri = ArchiveExtractionUtils.prepareExtractionDestination(
                context, 
                destinationUri, 
                subPath
            )
            
            while (entry != null) {
                if (!entry.isDirectory) {
                    val fileName = entry.name
                    val sanitizedFileName = ArchiveExtractionUtils.sanitizeFileName(fileName)
                    
                    val finalFileName = if (sanitizedFileName.contains("/")) {
                        val parts = sanitizedFileName.split("/")
                        if (parts.size > 1) {
                            parts.drop(1).joinToString("/")
                        } else {
                            sanitizedFileName
                        }
                    } else {
                        sanitizedFileName
                    }
                    
                    val outputUri = try {
                        val destinationDir = DocumentFile.fromTreeUri(context, finalDestinationUri)
                        if (destinationDir != null) {
                            val outputFile = destinationDir.createFile("application/octet-stream", finalFileName)
                            outputFile?.uri
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        Log.e("ArchiveExtractorService", "Error creating output file: ${e.message}")
                        null
                    }
                    
                    if (outputUri != null) {
                        val outputStream = context.contentResolver.openOutputStream(outputUri)
                        if (outputStream != null) {
                            val inputStream = sevenZFile.getInputStream(entry)
                            ArchiveExtractionUtils.copyStream(inputStream, outputStream)
                            inputStream.close()
                            outputStream.close()
                            extractedCount++
                            extractedFiles.add(finalFileName)
                            ArchiveExtractionUtils.logProgress(extractedCount, totalEntries, finalFileName)
                            onProgress(ArchiveExtractionUtils.calculateProgress(extractedCount, totalEntries))
                        }
                    }
                }
                entry = sevenZFile.nextEntry
            }
            
            sevenZFile.close()
            tempFile.delete()
            
            Log.d("ArchiveExtractorService", "Successfully extracted $extractedCount files")
            extractedFiles
            
        } catch (e: Exception) {
            Log.e("ArchiveExtractorService", "Error extracting 7Z: ${e.message}", e)
            emptyList()
        }
    }
}
