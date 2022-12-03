package com.auterion.tazama

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.auterion.tazama.data.VehicleViewModel

@Composable
fun Navigation(navController: NavHostController,
               vehicleViewModel : VehicleViewModel,
               modifier: Modifier) {

    NavHost(navController = navController, startDestination = AppDestinations.HomeDestination.route) {
        composable(AppDestinations.HomeDestination.route) {
            MapView(vehicleViewModel, modifier = modifier)
        }
    }
}
