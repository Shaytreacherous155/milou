package com.santiifm.milou.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "downloadable_files",
    foreignKeys = [
        ForeignKey(
            entity = ConsoleEntity::class,
            parentColumns = ["id"],
            childColumns = ["consoleId"],
            onDelete = ForeignKey.CASCADE
        )
  ],
    indices = [Index("consoleId")]
)
data class DownloadableFileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val fileName: String,
    val consoleId: String,
    val downloadUrl: String,
    val fileSize: Long = 0L,
    val fileExtension: String = "",
    @Ignore val extractedFiles: List<String> = emptyList()
) {
    constructor(
        id: Long,
        name: String,
        fileName: String,
        consoleId: String,
        downloadUrl: String,
        fileSize: Long,
        fileExtension: String
    ) : this(id, name, fileName, consoleId, downloadUrl, fileSize, fileExtension, emptyList())
}

