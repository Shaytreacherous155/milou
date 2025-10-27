package com.santiifm.milou.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santiifm.milou.data.repository.SettingsRepository
import com.santiifm.milou.ui.common.executeWithToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SettingsUiState(
    val downloadDirectory: String = "",
    val separateByConsole: Boolean = false,
    val limitSpeed: Float = Float.POSITIVE_INFINITY,
    val autoUnzip: Boolean = true,
    val concurrentDownloads: Int = 3,
    val isLoading: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {
    
    val uiState: StateFlow<SettingsUiState> = combine(
        repository.downloadDirectory,
        repository.separateByConsole,
        repository.limitSpeed,
        repository.autoUnzip,
        repository.concurrentDownloads
    ) { values: Array<Any> ->
        SettingsUiState(
            downloadDirectory = values[0] as String,
            separateByConsole = values[1] as Boolean,
            limitSpeed = values[2] as Float,
            autoUnzip = values[3] as Boolean,
            concurrentDownloads = values[4] as Int
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun onDownloadDirChanged(context: Context, path: String) {
        executeWithToast(context, "SettingsViewModel") {
            repository.updateDownloadDirectory(path)
        }
    }

    fun onSeparateByConsoleChanged(context: Context, enabled: Boolean) {
        executeWithToast(context, "SettingsViewModel") {
            repository.setSeparateByConsole(enabled)
        }
    }

    fun onLimitSpeedChanged(context: Context, limit: Float) {
        executeWithToast(context, "SettingsViewModel") {
            repository.setLimitSpeed(limit)
        }
    }

    fun onAutoUnzipChanged(context: Context, enabled: Boolean) {
        executeWithToast(context, "SettingsViewModel") {
            repository.setAutoUnzip(enabled)
        }
    }

    fun onConcurrentDownloadsChanged(context: Context, count: Int) {
        executeWithToast(context, "SettingsViewModel") {
            repository.setConcurrentDownloads(count)
        }
    }
}