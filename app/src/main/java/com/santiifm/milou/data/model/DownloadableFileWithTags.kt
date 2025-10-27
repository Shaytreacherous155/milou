package com.santiifm.milou.data.model

import com.santiifm.milou.data.local.entity.DownloadableFileEntity

data class DownloadableFileWithTags(
    val file: DownloadableFileEntity,
    val tags: List<String>
)
