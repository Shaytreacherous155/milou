package com.santiifm.milou.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.santiifm.milou.data.local.entity.DownloadableFileEntity
import com.santiifm.milou.data.local.entity.FileTagEntity
import kotlin.collections.getOrNull

@Dao
interface DownloadableFileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiles(files: List<DownloadableFileEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<FileTagEntity>)

    @Query("""
        SELECT df.*
        FROM downloadable_files df
        JOIN consoles c ON df.consoleId = c.id
        JOIN manufacturers m ON c.manufacturerId = m.id
        WHERE (:query = '*' OR df.name LIKE '%' || :query || '%')
          AND (:manufacturer IS NULL OR m.name = :manufacturer)
          AND (:consoleId IS NULL OR df.consoleId = :consoleId)
          AND (:tagsCount = 0 OR EXISTS (
                SELECT 1
                FROM downloadable_file_tags t
                WHERE t.fileId = df.id
                AND t.tag IN (:tags)
          ))
        ORDER BY
            CASE WHEN :sortAsc = 1 THEN df.name END ASC,
            CASE WHEN :sortAsc = 0 THEN df.name END DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun queryFiles(
        query: String,
        manufacturer: String?,
        consoleId: String?,
        tags: List<String>,
        tagsCount: Int,
        sortAsc: Boolean,
        limit: Int = 100,
        offset: Int = 0
    ): List<DownloadableFileEntity>

    @Query("SELECT COUNT(*) FROM downloadable_files")
    suspend fun getFilesCount(): Int

    @Query("""
        SELECT DISTINCT t.tag
        FROM downloadable_file_tags t
        JOIN downloadable_files df ON t.fileId = df.id
        JOIN consoles c ON df.consoleId = c.id
        JOIN manufacturers m ON c.manufacturerId = m.id
        WHERE (:query = '*' OR df.name LIKE '%' || :query || '%')
          AND (:manufacturer IS NULL OR m.name = :manufacturer)
          AND (:consoleId IS NULL OR df.consoleId = :consoleId)
        ORDER BY t.tag ASC
    """)
    suspend fun getAvailableTags(
        query: String,
        manufacturer: String?,
        consoleId: String?
    ): List<String>

    @Transaction
    suspend fun insertFilesWithTags(files: List<DownloadableFileEntity>, tags: List<FileTagEntity>) {
        val ids = insertAll(files)
        val tagsWithIds = tags.mapIndexed { index, tag ->
            tag.copy(fileId = ids.getOrNull(index) ?: 0L)
        }
        insertTags(tagsWithIds)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(files: List<DownloadableFileEntity>): List<Long>

    @Query("DELETE FROM downloadable_files")
    suspend fun clearAll()

    @Query("""
        SELECT df.id, df.name, df.fileName, df.consoleId, df.downloadUrl, df.fileSize, df.fileExtension, GROUP_CONCAT(t.tag, ',') as tags
        FROM downloadable_files df
        LEFT JOIN downloadable_file_tags t ON df.id = t.fileId
        JOIN consoles c ON df.consoleId = c.id
        JOIN manufacturers m ON c.manufacturerId = m.id
        WHERE (:query = '*' OR df.name LIKE '%' || :query || '%')
          AND (:manufacturer IS NULL OR m.name = :manufacturer)
          AND (:consoleId IS NULL OR df.consoleId = :consoleId)
          AND (:tagsCount = 0 OR df.id IN (
                SELECT DISTINCT t2.fileId
                FROM downloadable_file_tags t2
                WHERE t2.tag IN (:tags)
          ))
        GROUP BY df.id, df.name, df.fileName, df.consoleId, df.downloadUrl, df.fileSize, df.fileExtension
        ORDER BY
            CASE WHEN :sortAsc = 1 THEN df.name END ASC,
            CASE WHEN :sortAsc = 0 THEN df.name END DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun queryFilesWithTags(
        query: String,
        manufacturer: String?,
        consoleId: String?,
        tags: List<String>,
        tagsCount: Int,
        sortAsc: Boolean,
        limit: Int = 100,
        offset: Int = 0
    ): List<DownloadableFileWithTagsResult>
}

data class DownloadableFileWithTagsResult(
    val id: Long,
    val name: String,
    val fileName: String,
    val consoleId: String,
    val downloadUrl: String,
    val fileSize: Long,
    val fileExtension: String,
    val tags: String?
)
