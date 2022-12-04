package com.auterion.tazama.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.auterion.tazama.MapView
import com.auterion.tazama.data.VehicleViewModel
import com.auterion.tazama.presentation.pages.settings.SettingsPage

@Composable
fun Navigation(navController: NavHostController,
               vehicleViewModel : VehicleViewModel,
               modifier: Modifier) {

    NavHost(navController = navController, startDestination = HomeDestination.route) {
        composable(HomeDestination.route) {
            MapView(vehicleViewModel, modifier = modifier)
        }

        composable(SettingsDestination.route) {
            SettingsPage()
        }
    }
}
