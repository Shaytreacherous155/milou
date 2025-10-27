package com.santiifm.milou.ui.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiifm.milou.data.local.entity.ConsoleEntity
import com.santiifm.milou.data.local.dao.ConsoleWithFileCount
import com.santiifm.milou.data.model.DownloadableFileWithTags
import com.santiifm.milou.data.model.CategorizedTags
import com.santiifm.milou.data.repository.ConsoleRepository
import com.santiifm.milou.data.repository.DownloadableFileRepository
import com.santiifm.milou.data.repository.SettingsRepository
import com.santiifm.milou.data.service.DownloadService
import com.santiifm.milou.util.ConsoleFormatter
import com.santiifm.milou.util.StorageHelper
import com.santiifm.milou.util.ToastUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DownloadableFileRepository,
    private val consoleRepository: ConsoleRepository,
    private val downloadService: DownloadService,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _selectedConsoles = MutableStateFlow<Set<String>>(emptySet())
    val selectedConsoles: StateFlow<Set<String>> = _selectedConsoles

    var searchQuery = MutableStateFlow("")
    var activeTags = MutableStateFlow<Set<String>>(emptySet())
    var sortAsc = MutableStateFlow(true)
    
    private val _tagFilterMode = MutableStateFlow(FilterMode.OR)
    val tagFilterMode: StateFlow<FilterMode> = _tagFilterMode

    private val _results = MutableStateFlow<List<DownloadableFileWithTags>>(emptyList())
    val results: StateFlow<List<DownloadableFileWithTags>> = _results
    
    private val _consoles = MutableStateFlow<List<ConsoleEntity>>(emptyList())
    val consoles: StateFlow<List<ConsoleEntity>> = _consoles
    
    private val _consolesWithFiles = MutableStateFlow<List<ConsoleWithFileCount>>(emptyList())
    val consolesWithFiles: StateFlow<List<ConsoleWithFileCount>> = _consolesWithFiles
    
    private val _filteredConsoles = MutableStateFlow<List<ConsoleEntity>>(emptyList())

    private val _availableTags = MutableStateFlow<List<String>>(emptyList())
    val availableTags: StateFlow<List<String>> = _availableTags
    
    private val _categorizedTags = MutableStateFlow<CategorizedTags?>(null)
    val categorizedTags: StateFlow<CategorizedTags?> = _categorizedTags
    
    private val _hasMoreResults = MutableStateFlow(true)
    val hasMoreResults: StateFlow<Boolean> = _hasMoreResults
    
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore
    
    private var currentOffset = 0
    private val pageSize = 100

    init {
        viewModelScope.launch {
            combine(searchQuery, _selectedConsoles, activeTags, sortAsc, _tagFilterMode) { query, consoles, tags, asc, tagMode ->
                FilterParams(query, consoles, tags, asc, tagMode)
            }.collect { params ->
                currentOffset = 0
                
                val initialResults = performSearch(params)
                _results.value = initialResults
                _hasMoreResults.value = initialResults.size >= pageSize
                
                loadAvailableTags(params.query, null, if (params.consoles.size == 1) params.consoles.first() else null)
            }
        }
    }

    fun toggleConsoleFilter(consoleId: String) {
        val currentConsoles = _selectedConsoles.value.toMutableSet()
        if (currentConsoles.contains(consoleId)) {
            currentConsoles.remove(consoleId)
        } else {
            currentConsoles.add(consoleId)
        }
        _selectedConsoles.value = currentConsoles
    }
    
    fun clearConsoleFilters() {
        _selectedConsoles.value = emptySet()
    }

    fun setSearch(query: String) {
        searchQuery.value = query
    }
    
    fun toggleTag(tag: String) {
        val currentTags = activeTags.value.toMutableSet()
        if (currentTags.contains(tag)) {
            currentTags.remove(tag)
        } else {
            currentTags.add(tag)
        }
        activeTags.value = currentTags
    }
    
    fun removeTag(tag: String) {
        val currentTags = activeTags.value.toMutableSet()
        currentTags.remove(tag)
        activeTags.value = currentTags
    }
    
    fun removeConsole(consoleId: String) {
        val currentConsoles = _selectedConsoles.value.toMutableSet()
        currentConsoles.remove(consoleId)
        _selectedConsoles.value = currentConsoles
    }

    fun setSortAsc(ascending: Boolean) {
        sortAsc.value = ascending
    }
    
    fun clearAllFilters() {
        searchQuery.value = ""
        activeTags.value = emptySet()
        _selectedConsoles.value = emptySet()
        sortAsc.value = true
        _tagFilterMode.value = FilterMode.OR
    }
    
    fun toggleTagFilterMode() {
        _tagFilterMode.value = if (_tagFilterMode.value == FilterMode.OR) FilterMode.AND else FilterMode.OR
    }
    
    private suspend fun performSearch(params: FilterParams): List<DownloadableFileWithTags> {
        val (query, consoles, selectedTags, sortAsc, tagMode) = params
        
        currentOffset = 0
        
        return performSearchWithPagination(
            query = query,
            consoles = consoles,
            selectedTags = selectedTags,
            sortAsc = sortAsc,
            tagMode = tagMode,
            limit = pageSize,
            offset = 0
        )
    }
    
    suspend fun loadConsoles() {
        val allConsoles = consoleRepository.getAllConsoles().first()
        _consoles.value = allConsoles.sortedBy { 
            ConsoleFormatter.getConsoleDisplayName(it.id)
        }
        _filteredConsoles.value = _consoles.value
        
        // Load consoles that actually have files
        val consolesWithFiles = repository.getConsolesWithFiles(
            query = searchQuery.value.ifBlank { "*" },
            manufacturer = null
        )
        _consolesWithFiles.value = consolesWithFiles.sortedBy { 
            ConsoleFormatter.getConsoleDisplayName(it.id)
        }
    }
    
    private suspend fun loadAvailableTags(query: String, manufacturer: String?, consoleId: String?) {
        _availableTags.value = repository.getAvailableTags(
            query = query,
            manufacturer = manufacturer,
            consoleId = consoleId
        )
        
        _categorizedTags.value = repository.getCategorizedTags(
            query = query,
            manufacturer = manufacturer,
            consoleId = consoleId
        )
    }
    
    fun getConsoleName(consoleId: String): String {
        return ConsoleFormatter.formatConsoleField(consoleId)
    }
    
    suspend fun loadMore() {
        if (_isLoadingMore.value || !_hasMoreResults.value) return
        
        _isLoadingMore.value = true
        currentOffset += pageSize
        
        val newResults = performSearchWithPagination(
            query = searchQuery.value,
            consoles = _selectedConsoles.value,
            selectedTags = activeTags.value,
            sortAsc = sortAsc.value,
            tagMode = _tagFilterMode.value,
            limit = pageSize,
            offset = currentOffset
        )
        
        if (newResults.isEmpty()) {
            _hasMoreResults.value = false
        } else {
            _results.value = _results.value + newResults
            if (newResults.size < pageSize) {
                _hasMoreResults.value = false
            }
        }
        
        _isLoadingMore.value = false
    }
    
    private suspend fun performSearchWithPagination(
        query: String,
        consoles: Set<String>,
        selectedTags: Set<String>,
        sortAsc: Boolean,
        tagMode: FilterMode,
        limit: Int,
        offset: Int
    ): List<DownloadableFileWithTags> {
        val allResults = if (selectedTags.isEmpty() || tagMode == FilterMode.OR) {
            val results = mutableListOf<DownloadableFileWithTags>()
            if (consoles.isEmpty()) {
                results.addAll(repository.searchFilesWithTags(
                    query,
                    manufacturer = null,
                    consoleId = null,
                    tags = selectedTags,
                    sortAsc = sortAsc,
                    limit = limit * 2, // Get more results to account for deduplication
                    offset = offset
                ))
            } else {
                consoles.forEach { consoleId ->
                    results.addAll(repository.searchFilesWithTags(
                        query,
                        manufacturer = null,
                        consoleId = consoleId,
                        tags = selectedTags,
                        sortAsc = sortAsc,
                        limit = limit * 2, // Get more results per console
                        offset = offset
                    ))
                }
            }
            results.distinctBy { it.file.id }
        } else {
            val results = mutableListOf<DownloadableFileWithTags>()
            if (consoles.isEmpty()) {
                results.addAll(repository.searchFilesWithTags(
                    query,
                    manufacturer = null,
                    consoleId = null,
                    tags = emptySet(),
                    sortAsc = sortAsc,
                    limit = limit * 3, // Get more results for manual filtering
                    offset = offset
                ))
            } else {
                consoles.forEach { consoleId ->
                    results.addAll(repository.searchFilesWithTags(
                        query,
                        manufacturer = null,
                        consoleId = consoleId,
                        tags = emptySet(),
                        sortAsc = sortAsc,
                        limit = limit * 3,
                        offset = offset
                    ))
                }
            }
            results.distinctBy { it.file.id }
        }
        
        val finalResults = if (selectedTags.isEmpty() || tagMode == FilterMode.OR) {
            allResults
        } else {
            allResults.filter { fileWithTags ->
                val fileTags = fileWithTags.tags.toSet()
                selectedTags.all { selectedTag -> fileTags.contains(selectedTag) }
            }
        }
        
        return finalResults.sortedWith(
            compareBy<DownloadableFileWithTags> { if (sortAsc) it.file.name else "" }
                .thenBy { if (!sortAsc) it.file.name else "" }
        ).take(limit)
    }
    
    suspend fun startDownload(fileWithTags: DownloadableFileWithTags, context: Context) {
        val downloadDirectory = settingsRepository.downloadDirectory.first()
        if (downloadDirectory.isEmpty()) {
            ToastUtil.showError(context, "Download directory not configured. Please set a download folder in settings.")
            return
        }
        
        if (!StorageHelper.isValidUri(context, downloadDirectory)) {
            ToastUtil.showError(context, "Download directory is not accessible. Please check your folder permissions in settings.")
            return
        }
        
        downloadService.startDownload(fileWithTags.file)
    }
}

data class FilterParams(
    val query: String,
    val consoles: Set<String>,
    val tags: Set<String>,
    val sortAsc: Boolean,
    val tagMode: FilterMode
)

enum class FilterMode {
    AND, OR
}