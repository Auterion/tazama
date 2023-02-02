package com.auterion.tazama.presentation.pages.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.auterion.tazama.presentation.components.DropDown

@Composable
fun SettingsPage(settingsViewModel: SettingsViewModel) {
    val vehicleType = settingsViewModel.vehicleType.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
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

        val settingsViewModel = hiltViewModel<SettingsViewModel>()
        val items = settingsViewModel.mapTypes
        val currentItem = settingsViewModel.currentMapType.collectAsState()
        DropDownSetting(
            modifier = Modifier.padding(horizontal = 20.dp),
            label = "MapType",
            currentItem = currentItem.value,
            items = items,
            onItemSelected = { settingsViewModel.setSatelliteMap(it) }
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
