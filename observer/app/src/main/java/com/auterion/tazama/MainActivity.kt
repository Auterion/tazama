/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package com.auterion.tazama

import android.content.Context
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
import androidx.lifecycle.ViewModelProvider
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.auterion.tazama.libui.presentation.components.ExpandableFloatingActionButton
import com.auterion.tazama.libui.presentation.components.ExpandableFloatingActionButtonState
import com.auterion.tazama.libui.presentation.components.ExpandedItemAction
import com.auterion.tazama.libvehicle.PositionAbsolute
import com.auterion.tazama.libvehicle.VideoStreamInfo
import com.auterion.tazama.libviewmodel.TazamaBuilder
import com.auterion.tazama.libviewmodel.settings.SettingsViewModel
import com.auterion.tazama.libviewmodel.vehicle.VehicleViewModel
import com.auterion.tazama.navigation.MapDestination
import com.auterion.tazama.navigation.Navigation
import com.auterion.tazama.presentation.components.expandedItemsData
import com.auterion.tazama.presentation.pages.main.MainViewModel
import com.auterion.tazama.ui.theme.TazamaTheme
import kotlinx.coroutines.flow.StateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val player = provideVideoPlayer(application)
        val tazamaBuilder = TazamaBuilder(application, this)
        val settingsViewModel = tazamaBuilder.settingsViewModel
        val vehicleViewModel = tazamaBuilder.vehicleViewModel
        val mainViewModel = provideMainViewModel(player, vehicleViewModel.videoStreamInfo)

        setContent {
            TazamaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Main(player, mainViewModel, vehicleViewModel, settingsViewModel)
                }
            }
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun provideVideoPlayer(context: Context): ExoPlayer {
        val customLoadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(0, 0, 0, 0)
            .build()
        return ExoPlayer.Builder(context).setLoadControl(customLoadControl).build()
    }

    private fun provideMainViewModel(
        player: ExoPlayer,
        videoStreamInfo: StateFlow<VideoStreamInfo?>
    ): MainViewModel {
        return ViewModelProvider(
            this,
            MainViewModel.factory(player, videoStreamInfo)
        )[MainViewModel::class.java]
    }
}

@Composable
fun Main(
    player: ExoPlayer,
    mainViewModel: MainViewModel,
    vehicleViewModel: VehicleViewModel,
    settingsViewModel: SettingsViewModel,
) {
    val navController = rememberNavController()

    var floatingButtonState by remember {
        mutableStateOf(ExpandableFloatingActionButtonState.Collapsed)
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
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            mainViewModel,
            vehicleViewModel,
            settingsViewModel,
            player,
        )
    }
}
