package com.santiifm.milou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.santiifm.milou.ui.components.MilouFAB
import com.santiifm.milou.ui.components.MilouTopBar
import com.santiifm.milou.ui.navigation.NavRoutes
import com.santiifm.milou.ui.screens.contact.ContactScreen
import com.santiifm.milou.ui.screens.home.HomeScreen
import com.santiifm.milou.ui.screens.settings.SettingsScreen
import com.santiifm.milou.ui.screens.download.DownloadScreen
import com.santiifm.milou.ui.screens.sources.SourcesScreen
import com.santiifm.milou.ui.theme.MilouTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            MilouTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: NavRoutes.Home.route

                Scaffold(
                    topBar = { 
                        MilouTopBar(currentRoute, navController)
                    },
                    floatingActionButton = { 
                        MilouFAB(currentRoute, navController) 
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(NavRoutes.Home.route) { HomeScreen(navController) }
                        composable(NavRoutes.Downloads.route) { DownloadScreen(navController) }
                        composable(NavRoutes.Sources.route) { SourcesScreen() }
                        composable(NavRoutes.Settings.route) { SettingsScreen(navController) }
                        composable(NavRoutes.Contact.route) { ContactScreen(navController) }
                    }
                }
            }
        }
    }
}
