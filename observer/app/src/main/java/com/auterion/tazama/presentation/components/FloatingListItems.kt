package com.auterion.tazama.presentation.components

import com.auterion.tazama.R
import com.auterion.tazama.libui.presentation.components.ExpandedItemAction
import com.auterion.tazama.libui.presentation.components.ExpandedItemData
import com.auterion.tazama.navigation.SettingsDestination

val expandedItemsData = listOf(
    ExpandedItemData(
        iconId = R.drawable.baseline_settings_24,
        label = "Settings",
        action = ExpandedItemAction.ActionNavigate(SettingsDestination.route)
    ),
    ExpandedItemData(
        iconId = R.drawable.drone,
        label = "Center Vehicle",
        action = ExpandedItemAction.ActionCenterOnVehicle
    ),
    ExpandedItemData(
        iconId = R.drawable.diagonal_arrow,
        label = "Clear Flight Path",
        action = ExpandedItemAction.ActionClearFlightPath
    )
)
