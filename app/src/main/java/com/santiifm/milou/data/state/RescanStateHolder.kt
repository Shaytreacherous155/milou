package com.santiifm.milou.data.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RescanStateHolder @Inject constructor() {
    private val _isRescanning = MutableStateFlow(false)
    val isRescanning: StateFlow<Boolean> = _isRescanning.asStateFlow()
    
    private val _progressMessage = MutableStateFlow("")
    val progressMessage: StateFlow<String> = _progressMessage.asStateFlow()
    
    fun setRescanning(value: Boolean) {
        _isRescanning.value = value
    }
    
    fun setProgressMessage(message: String) {
        _progressMessage.value = message
    }
    
    fun clearProgressMessage() {
        _progressMessage.value = ""
    }
}
