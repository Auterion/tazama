/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package com.auterion.tazama.presentation.pages.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.auterion.tazama.libui.presentation.components.DropDown
import com.auterion.tazama.libviewmodel.settings.SettingsViewModel
import com.auterion.tazama.libviewmodel.settings.SettingsViewModel.MapType

@Composable
fun SettingsPage(settingsViewModel: SettingsViewModel) {
    val vehicleType = settingsViewModel.vehicleType.collectAsState()
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        CheckBoxSetting(
            modifier = Modifier.padding(horizontal = 20.dp),
            label = "Fake Vehicle Position",
            checked = vehicleType.value == SettingsViewModel.VehicleType.FAKE,
            onCheckedChanged = { isChecked ->
                if (isChecked) {
                    settingsViewModel.setVehicleType(SettingsViewModel.VehicleType.FAKE)
                } else {
                    settingsViewModel.setVehicleType(SettingsViewModel.VehicleType.MAVSDK)
                }
            }
        )

        val items = MapType.values().map { value -> value.toString() }
        val currentItem = settingsViewModel.currentMapType.collectAsState()
        val measureSystem = settingsViewModel.measureSystem.collectAsState()
        DropDownSetting(
            modifier = Modifier.padding(horizontal = 20.dp),
            label = "MapType",
            currentItem = currentItem.value.toString(),
            items = items,
            onItemSelected = { settingsViewModel.setSatelliteMap(MapType.valueOf(it)) }
        )

        DropDownSetting(
            modifier = Modifier.padding(horizontal = 20.dp),
            label = "Measure System",
            currentItem = measureSystem.value.toString(),
            items = SettingsViewModel.MeasureSystem.values().map { value -> value.toString() },
            onItemSelected = {
                settingsViewModel.setMeasureSystem(
                    SettingsViewModel.MeasureSystem.valueOf(
                        it
                    )
                )
            }
        )
    }
}

@Composable
fun CheckBoxSetting(
    modifier: Modifier,
    label: String,
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit
) {
    SettingRow(modifier = modifier, label = label) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onCheckedChanged(it) },
        )
    }
}

@Composable
fun DropDownSetting(
    modifier: Modifier,
    label: String,
    currentItem: String,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    SettingRow(modifier = modifier, label = label) {
        DropDown(
            items = items,
            selectedItem = currentItem
        ) {
            onItemSelected(it)
        }
    }
}

@Composable
fun SettingRow(
    modifier: Modifier,
    label: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        content()
    }
}
