package com.santiifm.milou.ui.screens.download

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.santiifm.milou.R
import com.santiifm.milou.data.model.DownloadItemModel
import com.santiifm.milou.data.model.DownloadStatus
import com.santiifm.milou.data.model.getStatusAssets
import com.santiifm.milou.util.FileParsingUtils
import com.santiifm.milou.util.iconColorFor
import kotlinx.coroutines.launch

@Composable
fun DownloadItem(
    modifier: Modifier = Modifier,
    item: DownloadItemModel,
    viewModel: DownloadViewModel = hiltViewModel()
) {
    val assets = item.getStatusAssets()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = assets.currentStatusIcon),
                contentDescription = item.status.name,
                tint = iconColorFor(assets.currentStatusIcon),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Remove file extension from the clean name
                val cleanNameWithoutExtension = item.name.substringBeforeLast(".")
                Text(
                    text = cleanNameWithoutExtension,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                val decodedFileName = FileParsingUtils.decodeUrlEncodedFileName(item.fileName)
                if (decodedFileName != item.fileName) {
                    Text(
                        text = decodedFileName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row {
                assets.availableStatusIcons.forEachIndexed { index, icon ->
                    val onClick: suspend () -> Unit = when (item.status) {
                        DownloadStatus.DOWNLOADING -> when (index) {
                            0 -> { { viewModel.cancelDownload(item.fileName) } } // Cancel Download Button
                            else -> { {} }
                        }
                        DownloadStatus.UNZIPPING -> when (index) {
                            0 -> { { viewModel.cancelDownload(item.fileName) } } // Cancel Unzipping Button
                            else -> { {} }
                        }
                        DownloadStatus.COMPLETED,
                        DownloadStatus.STOPPED,
                        DownloadStatus.FAILED -> when (index) {
                            0 -> { { viewModel.retryDownload(item.fileName) } } // Retry Button
                            1 -> { { viewModel.deleteDownloadWithConfirmation(item.fileName, item.status == DownloadStatus.COMPLETED) } } // Delete Button
                            else -> { {} }
                        }
                    }

                    ActionButton(
                        icon = icon,
                        onClick = onClick,
                        item = item,
                        index = index
                    )
                }
            }
        }
        
        if (item.status == DownloadStatus.DOWNLOADING || item.status == DownloadStatus.UNZIPPING) {
            Spacer(modifier = Modifier.width(8.dp))
            
            LinearProgressIndicator(
            progress = { item.progress },
            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
            color = ProgressIndicatorDefaults.linearColor,
            trackColor = ProgressIndicatorDefaults.linearTrackColor,
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (item.status == DownloadStatus.UNZIPPING) {
                        stringResource(R.string.extracting_files)
                    } else {
                        stringResource(R.string.download_speed, item.downloadSpeed)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "${formatFileSize(item.downloadedBytes)} / ${formatFileSize(item.fileSize)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else if (item.status == DownloadStatus.COMPLETED) {
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = stringResource(R.string.download_size, formatFileSize(item.fileSize)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> stringResource(R.string.file_size_bytes, bytes)
        bytes < 1024 * 1024 -> stringResource(R.string.file_size_kb, bytes / 1024.0)
        bytes < 1024 * 1024 * 1024 -> stringResource(R.string.file_size_mb, bytes / (1024.0 * 1024.0))
        else -> stringResource(R.string.file_size_gb, bytes / (1024.0 * 1024.0 * 1024.0))
    }
}

@Composable
private fun ActionButton(
    icon: Int,
    onClick: suspend () -> Unit,
    item: DownloadItemModel,
    index: Int
) {
    val scope = rememberCoroutineScope()
    
    IconButton(
        onClick = { scope.launch { onClick() } },
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = when (item.status) {
                DownloadStatus.DOWNLOADING -> when (index) {
                    0 -> stringResource(R.string.download_cancel)
                    else -> stringResource(R.string.download_action)
                }
                else -> when (index) {
                    0 -> stringResource(R.string.download_retry)
                    1 -> stringResource(R.string.download_delete)
                    else -> stringResource(R.string.download_action)
                }
            },
            tint = iconColorFor(icon),
            modifier = Modifier.size(20.dp)
        )
    }
}
