/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package com.auterion.tazama.libviewmodel.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.auterion.tazama.libui.presentation.pages.main.TelemetryDisplayNumber
import com.auterion.tazama.libui.presentation.pages.main.TelemetryInfo
import com.auterion.tazama.libviewmodel.vehicle.VehicleViewModel

@Composable
fun TelemetryInfo(
    modifier: Modifier = Modifier,
    vehicleViewModel: VehicleViewModel,
) {
    val distToHome =
        vehicleViewModel.horizontalDistanceToHome.collectAsState(TelemetryDisplayNumber())
    val heightAboveHome =
        vehicleViewModel.heightAboveHome.collectAsState(TelemetryDisplayNumber())
    val groundSpeed = vehicleViewModel.groundSpeed.collectAsState(TelemetryDisplayNumber())
    val heading = vehicleViewModel.vehicleHeading.collectAsState(TelemetryDisplayNumber())

    TelemetryInfo(
        modifier = modifier,
        distFromHome = distToHome.value,
        height = heightAboveHome.value,
        speed = groundSpeed.value,
        heading = heading.value,
    )
}
