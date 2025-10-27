package com.santiifm.milou.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "consoles",
    indices = [Index("manufacturerId")]
)
data class ConsoleEntity(
    @PrimaryKey val id: String, // e.g. "sony_psp"
    val name: String, // e.g. "PlayStation Portable"
    val manufacturerId: String, // e.g. "sony"
    val urls: String // JSON array of UrlEntry objects as string
)
