package com.auterion.tazama.presentation.pages.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsPage(settingsViewModel: SettingsViewModel) {
    val fakeVehiclePosition = settingsViewModel.fakeVehiclePosition.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Fake Vehicle Position",
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.fillMaxWidth())
            Checkbox(
                checked = fakeVehiclePosition.value,
                onCheckedChange = { settingsViewModel.setFakeVehiclePosition(it) })
        }
    }
}
