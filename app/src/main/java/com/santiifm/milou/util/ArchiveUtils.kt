package com.santiifm.milou.util

object ArchiveUtils {
    
    private val SUPPORTED_EXTENSIONS = setOf(
        ".zip", ".7z"
    )
    
    fun isExtractable(fileExtension: String): Boolean {
        if (fileExtension.isBlank()) return false
        val extension = fileExtension.lowercase()
        return SUPPORTED_EXTENSIONS.any { supportedExt ->
            extension.endsWith(supportedExt)
        }
    }
}
