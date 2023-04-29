package com.auterion.tazama.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.auterion.tazama.data.vehicle.VehicleViewModel
import com.auterion.tazama.presentation.pages.main.MainView
import com.auterion.tazama.presentation.pages.main.MainViewModel
import com.auterion.tazama.presentation.pages.settings.SettingsPage
import com.auterion.tazama.presentation.pages.settings.SettingsViewModel
import com.google.android.exoplayer2.ExoPlayer

@Composable
fun Navigation(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    vehicleViewModel: VehicleViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier,
    player: ExoPlayer
) {
    NavHost(navController = navController, startDestination = MapDestination.route) {
        composable(MapDestination.route) {
            MainView(mainViewModel, vehicleViewModel, player)
        }

        composable(SettingsDestination.route) {
            SettingsPage(settingsViewModel)
        }
    }
}
