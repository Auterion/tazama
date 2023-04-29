package com.auterion.tazama.presentation.pages.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.auterion.tazama.libui.presentation.pages.main.TelemetryDisplayNumber
import com.auterion.tazama.libui.presentation.pages.main.TelemetryInfo
import com.auterion.tazama.libui.presentation.pages.main.WindowDragger
import com.auterion.tazama.libvehicle.Degrees
import com.auterion.tazama.libvehicle.PositionAbsolute
import com.auterion.tazama.observer.R
import com.auterion.tazama.presentation.pages.settings.SettingsViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MainView(
    mainViewModel: MainViewModel,
    vehicleViewModel: VehicleViewModel,
    player: ExoPlayer
) {
    val settingsViewModel = hiltViewModel<SettingsViewModel>()

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

    val mSize = mainViewModel.mapSize.collectAsState()
    val vSize = mainViewModel.videoSize.collectAsState()
    val isLandScape = mainViewModel.isLandScape.collectAsState(false).value
    val mapZValue = mainViewModel.mapZValue.collectAsState(0.0F).value

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLandScape) {
            TelemetryComposable(
                modifier = Modifier
                    .zIndex(mapZValue + 1)
                    .align(Alignment.TopEnd)
                    .padding(10.dp),
                vehicleViewModel
            )
        }
        Box(
            modifier = Modifier
                .size(mSize.value.width.dp, mSize.value.height.dp)
                .zIndex(mapZValue)
                .align(if (isLandScape) Alignment.TopStart else Alignment.BottomCenter)
                .padding(if (mainViewModel.mapIsMainScreen || !isLandScape) 0.dp else 15.dp)
        ) {
            if (!isLandScape) {
                TelemetryComposable(
                    modifier = Modifier
                        .zIndex(mapZValue + 1)
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    vehicleViewModel
                )
            }
            MapComposable(mainViewModel, settingsViewModel, vehicleViewModel)

            if (!mainViewModel.mapIsMainScreen && mainViewModel.showDragIndicators) {
                WindowDragger(onDragAmount = {
                    mainViewModel.onUiEvent(ScreenEvent.MapWindowDrag(it))
                }, modifier = Modifier.align(Alignment.BottomEnd))
            }
        }

        Box(modifier = Modifier
            .background(color = Color.Transparent)
            .size(vSize.value.width.dp, vSize.value.height.dp)
            .align(Alignment.TopStart)
            .padding(if (!mainViewModel.mapIsMainScreen || !isLandScape) 0.dp else 15.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                mainViewModel.onUiEvent(ScreenEvent.VideoTapped)
            }) {
            VideoComposable(vSize)

            if (mainViewModel.mapIsMainScreen && mainViewModel.showDragIndicators) {
                WindowDragger(
                    onDragAmount =
                    {
                        mainViewModel.onUiEvent(
                            ScreenEvent.VideoWindowDrag(it)
                        )
                    },
                    modifier = Modifier.align(Alignment.BottomEnd)
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
private fun VideoComposable(size: State<Size>) {
    AndroidView(
        modifier = Modifier.size(width = size.value.width.dp, size.value.height.dp),
        factory = {
            StyledPlayerView(it).apply {
                this.player = player
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            }
        })
}
