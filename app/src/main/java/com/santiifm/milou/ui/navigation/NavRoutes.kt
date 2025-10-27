package com.santiifm.milou.ui.navigation

sealed class NavRoutes(val route: String, val label: String) {
    object Home : NavRoutes("home", "Home")
    object Downloads : NavRoutes("downloads", "Downloads")
    object Sources : NavRoutes("sources", "Sources")
    object Settings : NavRoutes("settings", "Settings")
    object Contact : NavRoutes("contact", "Contact")

    companion object {
        val allRoutes by lazy {
            listOf(Home, Downloads, Sources, Settings, Contact)
        }
    }
}
