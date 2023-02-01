package com.auterion.tazama.navigation

import com.auterion.tazama.R

interface NavDestination {
    val route: String
    val label: String
    val icon_source_id: Int
}

object MapDestination : NavDestination {
    override val route = "map"
    override val label = "Map"
    override val icon_source_id = R.drawable.baseline_map_24
}

object SettingsDestination : NavDestination {
    override val route = "settings"
    override val label = "Settings"
    override val icon_source_id = R.drawable.baseline_settings_24
}

val navBarDestinations = listOf<NavDestination>(
    MapDestination,
    SettingsDestination
)