package com.auterion.tazama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.auterion.tazama.data.vehicle.PositionAbsolute
import com.auterion.tazama.data.vehicle.VehicleViewModel
import com.auterion.tazama.navigation.MapDestination
import com.auterion.tazama.navigation.Navigation
import com.auterion.tazama.presentation.components.ExpandableFloatingActionButton
import com.auterion.tazama.presentation.components.ExpandableFloatingactionButtonState
import com.auterion.tazama.presentation.components.ExpandedItemAction
import com.auterion.tazama.presentation.components.expandedItemsData
import com.auterion.tazama.presentation.pages.main.MainViewModel
import com.auterion.tazama.presentation.pages.settings.SettingsViewModel
import com.auterion.tazama.ui.theme.TazamaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TazamaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Main()
                }
            }
        }
    }
}

@Composable
fun Main() {
    val navController = rememberNavController()
    val vehicleViewModel = hiltViewModel<VehicleViewModel>()
    val mainViewModel = hiltViewModel<MainViewModel>()
    mainViewModel.setVideoStreamInfoFlow(vehicleViewModel.videoStreamInfo)
    val settingsViewModel = hiltViewModel<SettingsViewModel>()

    var floatingButtonState by remember {
        mutableStateOf(ExpandableFloatingactionButtonState.Collapsed)
    }

    val currentRoute =
        navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry)

    val vehiclePosition =
        vehicleViewModel.vehiclePosition.collectAsState(initial = PositionAbsolute())

    Scaffold(
        floatingActionButton = {
            if (currentRoute.value?.destination?.route == MapDestination.route) {
                ExpandableFloatingActionButton(
                    buttonState = floatingButtonState,
                    onButtonStateChanged = {
                        floatingButtonState = it
                    },
                    items = expandedItemsData,
                    onItemClicked = { item ->

                        when (item.action) {
                            is ExpandedItemAction.ActionCenterOnVehicle -> {
                                vehiclePosition.value?.let { mainViewModel.centerOnPosition(it) }
                            }

                            is ExpandedItemAction.ActionNavigate -> {
                                navController.navigate("settings") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }

                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }

                            ExpandedItemAction.ActionClearFlightPath -> {
                                vehicleViewModel.resetFlightPath()
                            }
                        }
                    }
                )
            }
        },
    ) { innerPadding ->
        Navigation(
            navController = navController,
            mainViewModel,
            vehicleViewModel,
            settingsViewModel,
            modifier = Modifier.padding(innerPadding),
            mainViewModel.player
        )
    }
}



