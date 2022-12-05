package com.auterion.tazama.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

interface NavDestination {
    val route : String
    val label : String
    val icon : ImageVector
}

object HomeDestination : NavDestination {
    override val route = "home"
    override val label = "Home"
    override val icon = Icons.Filled.Home
}

object SettingsDestination : NavDestination {
    override val route = "settings"
    override val label = "Settings"
    override val icon = Icons.Filled.Settings
}


val navBarDestinations = listOf<NavDestination>(
    SettingsDestination
)