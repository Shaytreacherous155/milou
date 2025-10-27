package com.santiifm.milou.ui.screens.sources.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.santiifm.milou.data.model.ContentType

@Composable
fun AddUrlDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, ContentType) -> Unit
) {
    var url by remember { mutableStateOf("") }
    var contentType by remember { mutableStateOf(ContentType.GAME) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add URL") },
        text = {
            Column {
                Text("Enter the URL to scrape content from:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("https://example.com/games/") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                if (isLandscape) {
                    // Landscape: Stack vertically for better space usage
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Content Type:", style = MaterialTheme.typography.titleSmall)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Games",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (contentType == ContentType.GAME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Switch(
                                checked = contentType == ContentType.MISCELLANEOUS,
                                onCheckedChange = { 
                                    contentType = if (it) ContentType.MISCELLANEOUS else ContentType.GAME 
                                }
                            )
                            Text(
                                text = "Miscellaneous",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (contentType == ContentType.MISCELLANEOUS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    // Portrait: Use horizontal layout with better spacing
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Content Type:", style = MaterialTheme.typography.titleSmall)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Games",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (contentType == ContentType.GAME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Switch(
                                checked = contentType == ContentType.MISCELLANEOUS,
                                onCheckedChange = { 
                                    contentType = if (it) ContentType.MISCELLANEOUS else ContentType.GAME 
                                }
                            )
                            Text(
                                text = "Miscellaneous",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (contentType == ContentType.MISCELLANEOUS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (url.isNotBlank()) {
                        onConfirm(url.trim(), contentType)
                        onDismiss()
                    }
                },
                enabled = url.isNotBlank()
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
