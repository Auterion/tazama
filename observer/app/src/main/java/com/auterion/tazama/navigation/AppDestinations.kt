package com.auterion.tazama.navigation

import com.auterion.tazama.observer.R

interface NavDestination {
    val route: String
    val label: String
    val iconSourceId: Int
}

object MapDestination : NavDestination {
    override val route = "map"
    override val label = "Map"
    override val iconSourceId = R.drawable.baseline_map_24
}

object SettingsDestination : NavDestination {
    override val route = "settings"
    override val label = "Settings"
    override val iconSourceId = R.drawable.baseline_settings_24
}

val navBarDestinations = listOf(
    MapDestination,
    SettingsDestination,
)
