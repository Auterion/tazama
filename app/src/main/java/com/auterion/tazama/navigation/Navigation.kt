package com.auterion.tazama.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.auterion.tazama.data.vehicle.VehicleViewModel
import com.auterion.tazama.presentation.pages.main.MapView
import com.auterion.tazama.presentation.pages.settings.SettingsPage
import com.auterion.tazama.presentation.pages.settings.SettingsViewModel

@Composable
fun Navigation(
    navController: NavHostController,
    vehicleViewModel: VehicleViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier
) {

    NavHost(navController = navController, startDestination = HomeDestination.route) {
        composable(HomeDestination.route) {
            MapView(vehicleViewModel, modifier = modifier)
        }

        composable(SettingsDestination.route) {
            SettingsPage(settingsViewModel)
        }
    }
}
