package com.santiifm.milou.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.santiifm.milou.R
import com.santiifm.milou.data.local.entity.ConsoleEntity
import com.santiifm.milou.util.ConsoleFormatter

@Composable
fun ConsoleFilterDropdown(
    consoles: List<ConsoleEntity>,
    selectedConsoles: Set<String>,
    onConsoleToggle: (String) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.filter_by_console),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), // Set a maximum height for scrolling
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.all_consoles)) },
                        onClick = onClearFilters
                    )
                }
                
                if (consoles.isNotEmpty()) {
                    item {
                        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                    }
                    
                    items(consoles) { console ->
                        val consoleName = ConsoleFormatter.getConsoleDisplayName(console.id)
                        val isSelected = selectedConsoles.contains(console.id)
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    text = consoleName,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            },
                            onClick = { onConsoleToggle(console.id) }
                        )
                    }
                }
            }
        }
    }
}
