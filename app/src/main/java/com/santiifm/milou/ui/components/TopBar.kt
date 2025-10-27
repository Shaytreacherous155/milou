package com.santiifm.milou.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.santiifm.milou.R
import com.santiifm.milou.di.RescanStateEntryPoint
import com.santiifm.milou.ui.screens.sources.SourcesViewModel
import dagger.hilt.android.EntryPointAccessors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilouTopBar(
    currentRoute: String,
    navController: NavController
) {
    val context = LocalContext.current
    val rescanStateHolder = EntryPointAccessors.fromApplication(
        context.applicationContext,
        RescanStateEntryPoint::class.java
    ).rescanStateHolder()
    
    val isRescanning by rescanStateHolder.isRescanning.collectAsState()
    val progressMessage by rescanStateHolder.progressMessage.collectAsState()
    
    var showReloadButton by remember { mutableStateOf(false) }
    var hasPressedReload by remember { mutableStateOf(false) }
    var wasRescanning by remember { mutableStateOf(false) }

    val sourcesViewModel: SourcesViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        // Load default sources when the app starts
        sourcesViewModel.loadDefaultSources(context)
    }
    
    LaunchedEffect(isRescanning) {
        if (isRescanning) {
            wasRescanning = true
        } else if (wasRescanning && !hasPressedReload) {
            showReloadButton = true
        }
    }
    
    if (!isRescanning && !showReloadButton) {
        return
    }

    TopAppBar(
        title = {
        },
        actions = {
            if (isRescanning) {
                Row(
                    modifier = Modifier.padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (progressMessage.isNotEmpty()) progressMessage else "Rescanning sources...",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else if (showReloadButton && !hasPressedReload) {
                Row(
                    modifier = Modifier.padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            hasPressedReload = true
                            showReloadButton = false
                            navController.navigate(currentRoute) {
                                popUpTo(currentRoute) {
                                    inclusive = true
                                }
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_retry),
                            contentDescription = "Reload",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Reload",
                        modifier = Modifier.padding(start = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .height(56.dp)
            .statusBarsPadding()
    )
}