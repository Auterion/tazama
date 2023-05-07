package com.auterion.tazama.presentation.pages.main

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.auterion.tazama.data.vehicle.VehicleViewModel
import com.auterion.tazama.libui.presentation.components.VehicleMapMarker
import com.auterion.tazama.libui.presentation.pages.main.SwappableView
import com.auterion.tazama.libui.presentation.pages.main.TelemetryDisplayNumber
import com.auterion.tazama.libui.presentation.pages.main.TelemetryInfo
import com.auterion.tazama.libui.presentation.util.Orientation
import com.auterion.tazama.libvehicle.Degrees
import com.auterion.tazama.libvehicle.PositionAbsolute
import com.auterion.tazama.observer.R
import com.auterion.tazama.presentation.pages.settings.SettingsViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline

@Composable
fun MainView(
    mainViewModel: MainViewModel,
    vehicleViewModel: VehicleViewModel,
    player: ExoPlayer
) {
    val orientation = Orientation.observeOrientation()

    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        println("SPARTA - is LANDSCAPE")
        Box(modifier = Modifier.fillMaxSize()) {
            TelemetryComposable(
                modifier = Modifier
                    .zIndex(1F)
                    .align(Alignment.TopEnd)
                    .padding(horizontal = 10.dp, vertical = 5.125.dp),
                vehicleViewModel
            )
            SwappableView(
                overlayModifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 5.125.dp)
                    .defaultMinSize(minWidth = 200.dp, minHeight = 112.5.dp),
                overlayAlignment = Alignment.TopStart,
                overlayAspectRatio = 16F / 9F,
                view1 = {
                    Box(
                        modifier = Modifier.background(color = Color.Transparent)
                    ) {
                        VideoComposable()
                    }
                },
                view2 = {
                    val settingsViewModel = hiltViewModel<SettingsViewModel>()
                    MapComposable(mainViewModel, settingsViewModel, vehicleViewModel)
                },
            )
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            VideoComposable(
                modifier = Modifier.aspectRatio(16F / 9F)

            )
            Box {
                TelemetryComposable(
                    modifier = Modifier
                        .zIndex(1F)
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    vehicleViewModel
                )

                val settingsViewModel = hiltViewModel<SettingsViewModel>()
                MapComposable(mainViewModel, settingsViewModel, vehicleViewModel)
            }
        }
    }
}

@Composable
private fun TelemetryComposable(
    modifier: Modifier = Modifier,
    vehicleViewModel: VehicleViewModel,
) {
    val distToHome =
        vehicleViewModel.horizontalDistanceToHome.collectAsState(TelemetryDisplayNumber())
    val heightAboveHome = vehicleViewModel.heightAboveHome.collectAsState(TelemetryDisplayNumber())
    val groundSpeed = vehicleViewModel.groundSpeed.collectAsState(TelemetryDisplayNumber())
    val heading = vehicleViewModel.vehicleHeading.collectAsState(TelemetryDisplayNumber())

    TelemetryInfo(
        modifier = modifier,
        distFromHome = distToHome.value,
        height = heightAboveHome.value,
        speed = groundSpeed.value,
        heading = heading.value,
    )
}

@Composable
private fun MapComposable(
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel,
    vehicleViewModel: VehicleViewModel,
) {
    val mapType = settingsViewModel.currentMapType.collectAsState()
    val vehiclePosition = vehicleViewModel.vehiclePosition.collectAsState(PositionAbsolute())
    val vehicleAttitude = vehicleViewModel.vehicleAttitude.collectAsState()
    val vehiclePath = vehicleViewModel.vehiclePath.path.collectAsState(emptyList())
    val cameraPositionState = mainViewModel.cameraPositionState

    val props =
        MapProperties(
            mapType = when (mapType.value) {
                SettingsViewModel.MapType.SATELLITE -> MapType.SATELLITE
                SettingsViewModel.MapType.NORMAL -> MapType.NORMAL
                SettingsViewModel.MapType.HYBRID -> MapType.HYBRID
            }
        )

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = props,
        uiSettings = MapUiSettings(zoomControlsEnabled = false),
        onMapClick = { mainViewModel.onUiEvent(ScreenEvent.MapTapped) },
    ) {
        vehiclePosition.value?.let { position ->
            VehicleMapMarker(
                context = LocalContext.current,
                position = LatLng(position.lat.value, position.lon.value),
                title = "Vehicle",
                iconResourceId = R.drawable.plane,
                rotation = vehicleAttitude.value?.yaw?.toDegrees() ?: Degrees(),
            )
        }

        Polyline(points = vehiclePath.value, color = Color.Red)
    }
}

@Composable
private fun VideoComposable(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = {
            StyledPlayerView(it).apply {
                this.player = player
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            }
        })
}
