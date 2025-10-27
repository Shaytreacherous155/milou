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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santiifm.milou.R
import com.santiifm.milou.data.model.TagCategory
import com.santiifm.milou.ui.screens.home.HomeViewModel

@Composable
fun TagFilterDropdown(
    viewModel: HomeViewModel,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        val categorizedTags by viewModel.categorizedTags.collectAsState()
        val activeTags by viewModel.activeTags.collectAsState()
        val tagFilterMode by viewModel.tagFilterMode.collectAsState()
        
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
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.filter_by_tags),
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    FilterModeToggle(
                        currentMode = tagFilterMode,
                        onToggle = { viewModel.toggleTagFilterMode() },
                        isConsoleFilter = false
                    )
                }
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categorizedTags?.let { tags ->
                        // Regions
                        if (tags.regions.tags.isNotEmpty()) {
                            item {
                                TagCategorySection(
                                    category = tags.regions,
                                    activeTags = activeTags,
                                    onTagToggle = { viewModel.toggleTag(it) }
                                )
                            }
                        }
                        
                        // Video Standards
                        if (tags.videoStandards.tags.isNotEmpty()) {
                            item {
                                TagCategorySection(
                                    category = tags.videoStandards,
                                    activeTags = activeTags,
                                    onTagToggle = { viewModel.toggleTag(it) }
                                )
                            }
                        }
                        
                        // Content Types
                        if (tags.contentTypes.tags.isNotEmpty()) {
                            item {
                                TagCategorySection(
                                    category = tags.contentTypes,
                                    activeTags = activeTags,
                                    onTagToggle = { viewModel.toggleTag(it) }
                                )
                            }
                        }
                        
                        // Languages
                        if (tags.languages.tags.isNotEmpty()) {
                            item {
                                TagCategorySection(
                                    category = tags.languages,
                                    activeTags = activeTags,
                                    onTagToggle = { viewModel.toggleTag(it) }
                                )
                            }
                        }
                        
                        // File Types
                        if (tags.fileTypes.tags.isNotEmpty()) {
                            item {
                                TagCategorySection(
                                    category = tags.fileTypes,
                                    activeTags = activeTags,
                                    onTagToggle = { viewModel.toggleTag(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TagCategorySection(
    category: TagCategory,
    activeTags: Set<String>,
    onTagToggle: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_down),
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier
                    .rotate(if (isExpanded) 180f else 0f)
                    .padding(end = 8.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            
            // Show count of active tags in this category
            val activeCount = category.tags.count { it in activeTags }
            if (activeCount > 0) {
                Text(
                    text = "($activeCount)",
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
        category.tags.forEach { tag ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onTagToggle(tag)
                    }
                    .padding(vertical = 2.dp, horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = tag in activeTags,
                    onCheckedChange = { 
                        onTagToggle(tag)
                    },
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = tag,
                    modifier = Modifier.padding(start = 6.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
            }
        }
        
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    }
}
