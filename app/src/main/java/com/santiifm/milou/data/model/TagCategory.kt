package com.santiifm.milou.data.model

data class TagCategory(
    val name: String,
    val tags: List<String>
)

data class CategorizedTags(
    val regions: TagCategory,
    val languages: TagCategory,
    val videoStandards: TagCategory,
    val contentTypes: TagCategory,
    val fileTypes: TagCategory
)

object TagCategorizer {
    
    fun categorizeTags(tags: List<String>): CategorizedTags {
        val regions = mutableListOf<String>()
        val languages = mutableListOf<String>()
        val videoStandards = mutableListOf<String>()
        val contentTypes = mutableListOf<String>()
        val fileTypes = mutableListOf<String>()
        
        tags.forEach { tag ->
            val cleanTag = tag.trim().uppercase()
            
            when {
                isRegion(cleanTag) -> regions.add(tag)
                isLanguage(cleanTag) -> languages.add(tag)
                isVideoStandard(cleanTag) -> videoStandards.add(tag)
                isContentType(cleanTag) -> contentTypes.add(tag)
                isFileType(cleanTag) -> fileTypes.add(tag)
            }
        }
        
        return CategorizedTags(
            regions = TagCategory("Regions", regions.sorted()),
            languages = TagCategory("Languages", languages.sorted()),
            videoStandards = TagCategory("Video Standards", videoStandards.sorted()),
            contentTypes = TagCategory("Content Types", contentTypes.sorted()),
            fileTypes = TagCategory("File Types", fileTypes.sorted())
        )
    }
    
    private fun isRegion(tag: String): Boolean {
        val validRegions = setOf(
            "USA", "EUROPE", "JAPAN", "KOREA", "CHINA", "AUSTRALIA", "CANADA", "MEXICO",
            "BRAZIL", "GERMANY", "FRANCE", "SPAIN", "ITALY", "RUSSIA", "UK", "UNITED KINGDOM",
            "BRITAIN", "WORLD"
        )
        val validContinents = setOf("NORTH AMERICA", "SOUTH AMERICA", "EUROPE", "ASIA", "AFRICA", "OCEANIA")
        return tag in validRegions || tag in validContinents
    }
    
    private fun isLanguage(tag: String): Boolean {
        return tag.matches(Regex("^[A-Z]{2,3}$"))
    }
    
    private fun isVideoStandard(tag: String): Boolean {
        return tag in setOf("PAL", "NTSC", "NTSC-J", "NTSC-U", "NTSC-C")
    }
    
    private fun isContentType(tag: String): Boolean {
        return tag in setOf("DLC", "UPDATE", "TITLE UPDATE", "PATCH", "DEMO", "BETA", "PROTO", "GAME", "MISCELLANEOUS")
    }
    
    private fun isFileType(tag: String): Boolean {
        return tag.matches(Regex("^\\.[A-Z0-9]+$")) || tag in setOf("ZIP", "RAR", "7Z", "ISO", "BIN", "CUE")
    }
}
