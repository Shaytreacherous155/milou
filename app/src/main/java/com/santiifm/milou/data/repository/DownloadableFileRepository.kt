package com.santiifm.milou.data.repository

import com.santiifm.milou.data.local.dao.DownloadableFileDao
import com.santiifm.milou.data.local.dao.ConsoleWithFileCount
import com.santiifm.milou.data.local.entity.DownloadableFileEntity
import com.santiifm.milou.data.local.entity.FileTagEntity
import com.santiifm.milou.data.model.DownloadableFileWithTags
import com.santiifm.milou.data.model.CategorizedTags
import com.santiifm.milou.data.model.TagCategorizer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadableFileRepository @Inject constructor(
    private val dao: DownloadableFileDao
) {
    suspend fun searchFilesWithTags(
        query: String,
        manufacturer: String? = null,
        consoleId: String? = null,
        tags: Set<String> = emptySet(),
        sortAsc: Boolean = true,
        limit: Int = 100,
        offset: Int = 0
    ): List<DownloadableFileWithTags> {
        val results = dao.queryFilesWithTags(
            query = query.ifBlank { "*" },
            manufacturer = manufacturer,
            consoleId = consoleId,
            tags = tags.toList(),
            tagsCount = tags.size,
            sortAsc = sortAsc,
            limit = limit,
            offset = offset
        )
        
        return results.map { result ->
            DownloadableFileWithTags(
                file = DownloadableFileEntity(
                    id = result.id,
                    name = result.name,
                    fileName = result.fileName,
                    consoleId = result.consoleId,
                    downloadUrl = result.downloadUrl,
                    fileSize = result.fileSize,
                    fileExtension = result.fileExtension
                ),
                tags = result.tags?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
            )
        }
    }

    suspend fun isDatabaseEmpty(): Boolean = dao.getFilesCount() == 0

    suspend fun insertAll(files: List<DownloadableFileEntity>): List<Long> {
        return dao.insertAll(files)
    }

    suspend fun insertTags(tags: List<FileTagEntity>) {
        dao.insertTags(tags)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }

    suspend fun getAvailableTags(
        query: String,
        manufacturer: String? = null,
        consoleId: String? = null
    ): List<String> {
        return dao.getAvailableTags(
            query = query.ifBlank { "*" },
            manufacturer = manufacturer,
            consoleId = consoleId
        )
    }
    
    suspend fun getCategorizedTags(
        query: String,
        manufacturer: String? = null,
        consoleId: String? = null
    ): CategorizedTags {
        val allTags = dao.getAvailableTags(
            query = query.ifBlank { "*" },
            manufacturer = manufacturer,
            consoleId = consoleId
        )
        return TagCategorizer.categorizeTags(allTags)
    }
    
    suspend fun getConsolesWithFiles(
        query: String,
        manufacturer: String? = null
    ): List<ConsoleWithFileCount> {
        return dao.getConsolesWithFiles(
            query = query.ifBlank { "*" },
            manufacturer = manufacturer
        )
    }
}
