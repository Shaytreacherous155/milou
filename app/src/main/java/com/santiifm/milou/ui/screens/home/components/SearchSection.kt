package com.santiifm.milou.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.santiifm.milou.R
import com.santiifm.milou.ui.components.ClearFiltersButton
import com.santiifm.milou.ui.components.ConsoleFiltersButton
import com.santiifm.milou.ui.components.FilterButton
import com.santiifm.milou.ui.components.SortButton
import com.santiifm.milou.ui.components.common.VerticalSpacer
import com.santiifm.milou.ui.screens.home.HomeViewModel
import com.santiifm.milou.util.Constants

@Composable
fun SearchSection(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var showTagFilter by remember { mutableStateOf(false) }
    var showConsoleFilter by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }
    val sortAsc by viewModel.sortAsc.collectAsState()
    val selectedConsoles by viewModel.selectedConsoles.collectAsState()
    val selectedTags by viewModel.activeTags.collectAsState()
    val consoles by viewModel.consoles.collectAsState()
    val tagFilterMode by viewModel.tagFilterMode.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = modifier
            .padding(
                start = Constants.DEFAULT_PADDING_DP.dp,
                end = Constants.DEFAULT_PADDING_DP.dp,
                bottom = if (isLandscape) 4.dp else Constants.DEFAULT_PADDING_DP.dp,
                top = if (isLandscape) 4.dp else 0.dp
            ),
        verticalArrangement = Arrangement.spacedBy(if (isLandscape) 4.dp else 8.dp)
    ) {
        if (!isLandscape) {
            Text(
                text = "Search",
                style = MaterialTheme.typography.titleLarge
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { 
                    query = it
                    viewModel.setSearch(it)
                },
                label = { Text(stringResource(R.string.search_library)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.setSearch(query)
                    }
                ),
                modifier = Modifier.weight(1f)
            )
            
            IconButton(
                onClick = { showFilters = !showFilters }
            ) {
                Icon(
                    painter = painterResource(if (showFilters) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down),
                    contentDescription = if (showFilters) "Hide filters" else "Show filters"
                )
            }
        }
        if (isLandscape) {
            VerticalSpacer(height = 4.dp)
        } else {
            VerticalSpacer()
        }
        
        if (showFilters) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    ConsoleFiltersButton(
                        onClick = { showConsoleFilter = !showConsoleFilter },
                        modifier = Modifier.padding(end = Constants.BUTTON_SPACING_DP.dp)
                    )
                    
                    SortButton(
                        onClick = { viewModel.setSortAsc(!sortAsc) },
                        modifier = Modifier.padding(end = Constants.BUTTON_SPACING_DP.dp)
                    )
                    
                    FilterButton(
                        onClick = { showTagFilter = !showTagFilter }
                    )
                }
                
                ClearFiltersButton(
                    onClick = {
                        viewModel.clearAllFilters()
                        query = ""
                    }
                )
            }
        }
        
        if (showFilters) {
            if (showTagFilter) {
                TagFilterDropdown(
                    viewModel = viewModel,
                    isVisible = showTagFilter
                )
            }
            
            if (showConsoleFilter) {
                ConsoleFilterDropdown(
                    consoles = consoles,
                    selectedConsoles = selectedConsoles,
                    onConsoleToggle = { consoleId ->
                        viewModel.toggleConsoleFilter(consoleId)
                    },
                    onClearFilters = {
                        viewModel.clearConsoleFilters()
                        showConsoleFilter = false
                    }
                )
            }

            SelectedFiltersPills(
                selectedConsoles = selectedConsoles,
                selectedTags = selectedTags,
                tagFilterMode = tagFilterMode,
                getConsoleName = viewModel::getConsoleName,
                onRemoveConsole = viewModel::removeConsole,
                onRemoveTag = viewModel::removeTag,
                modifier = Modifier.padding(top = if (isLandscape) 4.dp else 8.dp)
            )
        }
    }
}
