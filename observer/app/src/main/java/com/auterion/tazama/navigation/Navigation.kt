package com.auterion.tazama.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.auterion.tazama.data.vehicle.VehicleViewModel
import com.auterion.tazama.presentation.pages.main.MainView
import com.auterion.tazama.presentation.pages.main.MainViewModel
import com.auterion.tazama.presentation.pages.settings.SettingsPage
import com.auterion.tazama.presentation.pages.settings.SettingsViewModel

@Composable
fun Navigation(
    modifier: Modifier,
    navController: NavHostController,
    mainViewModel: MainViewModel,
    vehicleViewModel: VehicleViewModel,
    settingsViewModel: SettingsViewModel,
    player: ExoPlayer
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MapDestination.route
    ) {
        composable(MapDestination.route) {
            MainView(player, mainViewModel, vehicleViewModel, settingsViewModel)
        }

        composable(SettingsDestination.route) {
            SettingsPage(settingsViewModel)
        }
    }
}
