/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package com.auterion.tazama.presentation.components

import com.auterion.tazama.libui.presentation.components.ExpandedItemAction
import com.auterion.tazama.libui.presentation.components.ExpandedItemData
import com.auterion.tazama.navigation.SettingsDestination
import com.auterion.tazama.observer.R

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
