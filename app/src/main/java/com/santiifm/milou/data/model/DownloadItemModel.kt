package com.santiifm.milou.data.model

import com.santiifm.milou.R

enum class DownloadStatus {
    DOWNLOADING, 
    UNZIPPING,
    COMPLETED, 
    FAILED,
    STOPPED
}

data class StatusAssets(
    val currentStatusIcon: Int,
    val availableStatusIcons: IntArray
)

data class DownloadItemModel(
    val name: String,
    val fileName: String,
    val downloadSpeed: Float, // MB/s
    val progress: Float, // 0f..1f
    val fileSize: Long, // bytes
    val downloadedBytes: Long = 0L, // bytes downloaded so far
    val status: DownloadStatus
)
fun DownloadItemModel.getStatusAssets(): StatusAssets = when (status) {
    DownloadStatus.DOWNLOADING ->
        StatusAssets(
            currentStatusIcon = R.drawable.ic_arrow_down,
            availableStatusIcons = intArrayOf(
                R.drawable.ic_stop
            )
        )
    DownloadStatus.UNZIPPING ->
        StatusAssets(
            currentStatusIcon = R.drawable.ic_extract,
            availableStatusIcons = intArrayOf(
                R.drawable.ic_stop
            )
        )
    DownloadStatus.COMPLETED ->
        StatusAssets(
            currentStatusIcon = R.drawable.ic_check,
            availableStatusIcons = intArrayOf(
                R.drawable.ic_retry,
                R.drawable.ic_trash
            )
        )
    DownloadStatus.FAILED ->
        StatusAssets(
            currentStatusIcon = R.drawable.ic_error,
            availableStatusIcons = intArrayOf(
                R.drawable.ic_retry,
                R.drawable.ic_trash
            )
        )
    DownloadStatus.STOPPED ->
        StatusAssets(
            currentStatusIcon = R.drawable.ic_stop,
            availableStatusIcons = intArrayOf(
                R.drawable.ic_retry,
                R.drawable.ic_trash
            )
        )
}
