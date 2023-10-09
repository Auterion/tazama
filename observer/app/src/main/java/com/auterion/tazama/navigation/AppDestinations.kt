/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

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
