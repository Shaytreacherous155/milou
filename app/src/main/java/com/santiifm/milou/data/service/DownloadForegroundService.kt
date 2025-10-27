package com.santiifm.milou.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.santiifm.milou.MainActivity
import com.santiifm.milou.R
import com.santiifm.milou.data.model.DownloadItemModel
import com.santiifm.milou.data.model.DownloadStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DownloadForegroundService : Service() {
    
    @Inject
    lateinit var downloadService: DownloadService
    
    @Inject
    lateinit var downloadProgressTracker: DownloadProgressTracker
    
    private val notificationManager by lazy { 
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }
    
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var isServiceRunning = false
    private val _activeDownloads = MutableStateFlow<List<DownloadItemModel>>(emptyList())

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "download_channel"
        const val ACTION_START_SERVICE = "start_service"
        const val ACTION_STOP_SERVICE = "stop_service"
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        
        coroutineScope.launch {
            downloadService.downloads.collect { downloads ->
                val activeDownloads = downloads.filter { 
                    it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.UNZIPPING 
                }
                _activeDownloads.value = activeDownloads
                
                if (isServiceRunning) {
                    updateNotification(activeDownloads)
                }
            }
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SERVICE -> {
                startForegroundService()
            }
            ACTION_STOP_SERVICE -> {
                stopForegroundService()
            }
        }
        return START_STICKY
    }
    
    private fun startForegroundService() {
        isServiceRunning = true
        val currentDownloads = downloadService.getDownloads().filter { 
            it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.UNZIPPING 
        }
        _activeDownloads.value = currentDownloads
        val notification = createNotification(currentDownloads)
        startForeground(NOTIFICATION_ID, notification)
    }
    
    private fun stopForegroundService() {
        isServiceRunning = false
        stopForeground(true)
        stopSelf()
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Download Progress",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows download progress for ROM files"
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }
    
    private fun createNotification(downloads: List<DownloadItemModel>): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return when {
            downloads.isEmpty() -> {
                NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Milou Downloader")
                    .setContentText("No active downloads")
                    .setSmallIcon(R.drawable.milou)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build()
            }
            downloads.size == 1 -> {
                val download = downloads.first()
                createSingleDownloadNotification(download, pendingIntent)
            }
            else -> {
                createMultipleDownloadsNotification(downloads, pendingIntent)
            }
        }
    }
    
    private fun createSingleDownloadNotification(
        download: DownloadItemModel, 
        pendingIntent: PendingIntent
    ): Notification {
        val progress = downloadProgressTracker.calculateProgressPercentage(download.progress)
        val speed = String.format("%.1f MB/s", download.downloadSpeed)
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(download.name)
            .setContentText("$progress% • $speed")
            .setSmallIcon(R.drawable.milou)
            .setContentIntent(pendingIntent)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }
    
    private fun createMultipleDownloadsNotification(
        downloads: List<DownloadItemModel>, 
        pendingIntent: PendingIntent
    ): Notification {
        val totalDownloads = downloads.size
        val completedDownloads = downloads.count { it.status == DownloadStatus.COMPLETED }
        
        val inboxStyle = NotificationCompat.InboxStyle()
            .setBigContentTitle("Downloading $totalDownloads files")
            .setSummaryText("$completedDownloads/$totalDownloads completed")
        
        downloads.take(5).forEach { download -> // Limit to 5 to avoid notification size limits
            val progress = downloadProgressTracker.calculateProgressPercentage(download.progress)
            val speed = String.format("%.1f MB/s", download.downloadSpeed)
            val status = when (download.status) {
                DownloadStatus.DOWNLOADING -> "Downloading"
                DownloadStatus.UNZIPPING -> "Extracting"
                else -> "Processing"
            }
            
            inboxStyle.addLine("$status: ${download.name} ($progress% • $speed)")
        }
        
        if (downloads.size > 5) {
            inboxStyle.addLine("... and ${downloads.size - 5} more")
        }
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Downloading $totalDownloads files")
            .setContentText("$completedDownloads/$totalDownloads completed")
            .setSmallIcon(R.drawable.milou)
            .setContentIntent(pendingIntent)
            .setStyle(inboxStyle)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }
    
    private fun updateNotification(downloads: List<DownloadItemModel>) {
        val notification = createNotification(downloads)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    override fun onBind(intent: Intent?) = null
}
