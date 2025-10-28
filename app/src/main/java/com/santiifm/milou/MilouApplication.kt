package com.santiifm.milou

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MilouApplication : Application() {
    
    @Inject
    lateinit var versionCheckerService: com.santiifm.milou.data.service.VersionCheckerService
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        
        // Check for updates on app startup
        applicationScope.launch {
            versionCheckerService.checkForUpdates(this@MilouApplication)
        }
    }
}