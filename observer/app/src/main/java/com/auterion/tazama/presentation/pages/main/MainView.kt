package com.auterion.tazama.presentation.pages.main

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.auterion.tazama.libui.presentation.pages.main.SwappableView
import com.auterion.tazama.libvehicle.Degrees
import com.auterion.tazama.libvehicle.PositionAbsolute
import com.auterion.tazama.libviewmodel.presentation.components.TelemetryInfo
import com.auterion.tazama.libviewmodel.settings.SettingsViewModel
import com.auterion.tazama.libviewmodel.util.mapState
import com.auterion.tazama.libviewmodel.vehicle.VehicleViewModel
import com.auterion.tazama.observer.R
import com.auterion.tazama.presentation.components.VehicleMapMarker
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline

@Composable
fun MainView(
    player: ExoPlayer,
    mainViewModel: MainViewModel,
    vehicleViewModel: VehicleViewModel,
    settingsViewModel: SettingsViewModel,
) {
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Box(modifier = Modifier.fillMaxSize()) {
            TelemetryInfo(
                modifier = Modifier
                    .zIndex(1F)
                    .align(Alignment.TopEnd)
                    .padding(horizontal = 10.dp, vertical = 5.125.dp),
                vehicleViewModel
            )
            SwappableView(
                overlayModifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 5.125.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .defaultMinSize(minWidth = 200.dp, minHeight = 112.5.dp),
                overlayAlignment = Alignment.TopStart,
                overlayAspectRatio = 16F / 9F,
                view1 = {
                    Box(
                        modifier = Modifier.background(color = Color.Transparent)
                    ) {
                        VideoComposable(player)
                    }
                },
                view2 = {
                    MapComposable(mainViewModel, settingsViewModel, vehicleViewModel)
                },
            )
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            VideoComposable(player, Modifier.aspectRatio(16F / 9F))
            Box {
                TelemetryInfo(
                    modifier = Modifier
                        .zIndex(1F)
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    vehicleViewModel
                )

                MapComposable(mainViewModel, settingsViewModel, vehicleViewModel)
            }
        }
    }
}

@Composable
private fun MapComposable(
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel,
    vehicleViewModel: VehicleViewModel,
) {
    val mapType = settingsViewModel.currentMapType.collectAsState()
    val cameraPositionState = mainViewModel.cameraPositionState
    val vehiclePosition = vehicleViewModel.vehiclePosition.collectAsState(PositionAbsolute())
    val vehicleAttitude = vehicleViewModel.vehicleAttitude.collectAsState()
    val vehiclePath = vehicleViewModel.vehiclePath.path.mapState { path ->
        path.map { latLng -> LatLng(latLng.latitude, latLng.longitude) }
    }.collectAsState()

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
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            compassEnabled = false,
            rotationGesturesEnabled = false,
        ),
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
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
private fun VideoComposable(player: ExoPlayer, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = {
            PlayerView(it).apply {
                this.player = player
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            }
        })
}
