package com.auterion.tazama.navigation

import android.widget.ImageButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

interface AppDestination {
    val route : String
    val label : String
    val icon : ImageVector
}

object HomeDestination : AppDestination {
    override val route = "home"
    override val label = "Home"
    override val icon = Icons.Filled.Home
}

object SettingsDestination : AppDestination {
    override val route = "settings"
    override val label = "Settings"
    override val icon = Icons.Filled.Settings
}


val destinations = listOf<AppDestination>(
    SettingsDestination
)