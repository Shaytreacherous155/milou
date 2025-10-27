package com.santiifm.milou.data.service

import android.content.Context
import com.santiifm.milou.data.local.dao.ConsoleDao
import com.santiifm.milou.data.local.dao.ManufacturerDao
import com.santiifm.milou.data.local.entity.ConsoleEntity
import com.santiifm.milou.data.local.entity.ManufacturerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSourcesLoader @Inject constructor(
    private val manufacturerDao: ManufacturerDao,
    private val consoleDao: ConsoleDao
) {
    
    suspend fun loadDefaultSourcesToDatabase(context: Context) = withContext(Dispatchers.IO) {
        try {
            consoleDao.clearAll()
            manufacturerDao.clearAll()
            
            val jsonString = context.assets.open("consoles.json").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)

            val manufacturers = mutableListOf<ManufacturerEntity>()
            val consoles = mutableListOf<ConsoleEntity>()

            jsonObject.keys().forEach { manufacturerName ->
                val manufacturerObj = jsonObject.getJSONObject(manufacturerName)

                val manufacturer = ManufacturerEntity(
                    id = manufacturerName,
                    name = formatManufacturerName(manufacturerName)
                )
                manufacturers.add(manufacturer)

                manufacturerObj.keys().forEach { consoleName ->
                    val consoleObj = manufacturerObj.getJSONObject(consoleName)
                    val urlEntries = mutableListOf<JSONObject>()

                    // Use new "urls" format
                    val urlsArray = consoleObj.getJSONArray("urls")
                    for (i in 0 until urlsArray.length()) {
                        val urlObj = urlsArray.getJSONObject(i)
                        val urlEntry = JSONObject().apply {
                            put("url", urlObj.getString("url"))
                            put("contentType", urlObj.optString("contentType", "GAME"))
                        }
                        urlEntries.add(urlEntry)
                    }

                    val console = ConsoleEntity(
                        id = "${manufacturerName}_${consoleName}",
                        name = formatConsoleName(consoleName),
                        manufacturerId = manufacturerName,
                        urls = JSONArray(urlEntries).toString()
                    )
                    consoles.add(console)
                }
            }

            println("Inserting ${manufacturers.size} manufacturers...")
            manufacturerDao.insertManufacturers(manufacturers)
            
            println("Inserting ${consoles.size} consoles...")
            consoleDao.insertConsoles(consoles)

            println("Loaded ${manufacturers.size} manufacturers and ${consoles.size} consoles into database")
        } catch (e: Exception) {
            println("Error loading default sources: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun formatManufacturerName(name: String): String {
        return name.split("_")
            .joinToString(" ") { word ->
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
    }
    
    private fun formatConsoleName(name: String): String {
        return name.split("_")
            .joinToString(" ") { word ->
                when (word.lowercase()) {
                    "snes" -> "SNES"
                    "ps1", "ps2", "ps3", "ps4", "ps5" -> word.uppercase()
                    "n64" -> "N64"
                    "gb", "gbc", "gba" -> word.uppercase()
                    else -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                }
            }
    }
}
