package com.santiifm.milou

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MilouApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}