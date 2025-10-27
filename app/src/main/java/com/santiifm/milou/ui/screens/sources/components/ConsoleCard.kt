package com.santiifm.milou.ui.screens.sources.components

import androidx.compose.foundation.layout.*
import androidx.compose.ui.res.painterResource
import com.santiifm.milou.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.santiifm.milou.data.model.Console

@Composable
fun ConsoleCard(
    console: Console,
    onAddUrl: () -> Unit,
    onEditConsole: () -> Unit,
    onDeleteConsole: () -> Unit,
    onDeleteUrl: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = console.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Row {
                    IconButton(onClick = onEditConsole) {
                        Icon(painterResource(R.drawable.ic_edit), contentDescription = "Edit Console")
                    }
                    IconButton(onClick = onDeleteConsole) {
                        Icon(painterResource(R.drawable.ic_trash), contentDescription = "Delete Console")
                    }
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            painterResource(if (expanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down),
                            contentDescription = if (expanded) "Collapse" else "Expand"
                        )
                    }
                }
            }
            
            Text(
                text = "${console.urls.size} URL${if (console.urls.size != 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            OutlinedButton(
                onClick = onAddUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Icon(painterResource(R.drawable.ic_add), contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add URL", style = MaterialTheme.typography.bodySmall)
            }
            
            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    console.urls.forEachIndexed { index, urlEntry ->
                        UrlItem(
                            urlEntry = urlEntry,
                            onDelete = { onDeleteUrl(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UrlItem(
    urlEntry: com.santiifm.milou.data.model.UrlEntry,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = urlEntry.url,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Type: ${urlEntry.contentType.name.lowercase()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    painterResource(R.drawable.ic_trash),
                    contentDescription = "Delete URL",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
