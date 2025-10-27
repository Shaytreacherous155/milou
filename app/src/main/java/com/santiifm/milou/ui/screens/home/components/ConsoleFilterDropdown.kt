package com.santiifm.milou.ui.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.santiifm.milou.R
import com.santiifm.milou.ui.screens.home.HomeViewModel
import com.santiifm.milou.util.ConsoleFormatter

@Composable
fun ConsoleFilterDropdown(
    viewModel: HomeViewModel,
    selectedConsoles: Set<String>,
    onConsoleToggle: (String) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val consolesWithFiles by viewModel.consolesWithFiles.collectAsState()
    var isExpanded by remember { mutableStateOf(true) }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.filter_by_console),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Show count of active console filters
                if (selectedConsoles.isNotEmpty()) {
                    Text(
                        text = "(${selectedConsoles.size})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(300))
            ) {
                Column {
                    // Clear all option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onClearFilters() }
                            .padding(vertical = 2.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedConsoles.isEmpty(),
                            onCheckedChange = { onClearFilters() },
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = stringResource(R.string.all_consoles),
                            modifier = Modifier.padding(start = 6.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    if (consolesWithFiles.isNotEmpty()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                        
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            items(consolesWithFiles) { console ->
                                val consoleName = ConsoleFormatter.getConsoleDisplayName(console.id)
                                val isSelected = selectedConsoles.contains(console.id)
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onConsoleToggle(console.id) }
                                        .padding(vertical = 2.dp, horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { onConsoleToggle(console.id) },
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = consoleName,
                                        modifier = Modifier.padding(start = 6.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    
                                    // Show file count
                                    Text(
                                        text = "(${console.fileCount})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 2.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No consoles with files found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
