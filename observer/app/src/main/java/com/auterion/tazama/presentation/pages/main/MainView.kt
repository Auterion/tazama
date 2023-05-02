package com.auterion.tazama.presentation.pages.main

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
    val screenSize =
        Size(
            LocalConfiguration.current.screenWidthDp.toFloat(),
            LocalConfiguration.current.screenHeightDp.toFloat()
        )

    LaunchedEffect(key1 = screenSize) {
        mainViewModel.onUiEvent(
            ScreenEvent.ScreenSizeChanged(screenSize)
        )
    }

    // TODO: refactor that
    var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }
    val configuration = LocalConfiguration.current

    // If our configuration changes then this will launch a new coroutine scope for it
    LaunchedEffect(configuration) {
        // Save any changes to the orientation value on the configuration object
        snapshotFlow { configuration.orientation }
            .collect { orientation = it }
    }

    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        val mapZValue = mainViewModel.mapZValue.collectAsState(0.0F).value

        Box(modifier = Modifier.fillMaxSize()) {
            TelemetryComposable(
                modifier = Modifier
                    .zIndex(mapZValue + 1)
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
            val vSize = mainViewModel.videoSize.collectAsState()
            VideoComposable(
                modifier = Modifier.size(
                    width = vSize.value.width.dp,
                    height = vSize.value.height.dp,
                )
            )
            Box {
                TelemetryComposable(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    vehicleViewModel
                )
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
