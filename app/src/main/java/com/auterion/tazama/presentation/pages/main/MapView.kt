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
import com.auterion.tazama.R
import com.auterion.tazama.data.vehicle.*
import com.auterion.tazama.presentation.components.VehicleMapMarker
import com.auterion.tazama.presentation.pages.settings.SettingsViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapView(
    mainViewModel: MainViewModel,
    vehicleViewModel: VehicleViewModel,
    player: ExoPlayer
) {
    val settingsViewModel = hiltViewModel<SettingsViewModel>()
    val mapType = settingsViewModel.currentMapType.collectAsState()

    val vehiclePosition = vehicleViewModel.vehiclePosition.collectAsState(PositionAbsolute())
    val cameraPositionState = rememberCameraPositionState {
        vehiclePosition.value?.let {
            position = CameraPosition.fromLatLngZoom(LatLng(it.lat.value, it.lon.value), 10f)
        }
    }

    val props =
        MapProperties(
            mapType = when (mapType.value) {
                SettingsViewModel.MapType.SATELLITE -> MapType.SATELLITE
                SettingsViewModel.MapType.NORMAL -> MapType.NORMAL
                SettingsViewModel.MapType.HYBRID -> MapType.HYBRID
            }
        )

    val screenSize =
        Size(
            LocalConfiguration.current.screenWidthDp.toFloat(),
            LocalConfiguration.current.screenHeightDp.toFloat()
        )

    LaunchedEffect(key1 = screenSize) {
        mainViewModel.onUiEvent(
            ScreenEvent.ScreenSizeChanged(
                screenSize
            )
        )
    }

    val mSize = mainViewModel.mapSize.collectAsState()
    val vSize = mainViewModel.videoSize.collectAsState()
    val mapZValue = mainViewModel.mapZValue.collectAsState(0.0F).value
    val isLandScape = mainViewModel.isLandScape.collectAsState(false).value
    val attitude = vehicleViewModel.vehicleAttitude.collectAsState()
    val distToHome =
        vehicleViewModel.horizonalDistanceToHome.collectAsState(TelemetryDisplayNumber())
    val heightAboveHome =
        vehicleViewModel.heightAboveHome.collectAsState(TelemetryDisplayNumber())
    val groundSpeed = vehicleViewModel.groundSpeed.collectAsState(TelemetryDisplayNumber())
    val heading = vehicleViewModel.vehicleHeading.collectAsState(initial = TelemetryDisplayNumber())
    val vehiclePath = vehicleViewModel.vehiclePath.collectAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLandScape) {
            TelemetryInfo(
                modifier = Modifier
                    .zIndex(mapZValue + 1)
                    .align(Alignment.TopEnd)
                    .padding(10.dp),
                distFromHome = distToHome.value,
                height = heightAboveHome.value,
                speed = groundSpeed.value,
                heading = heading.value,
            )
        }
        Box(
            modifier = Modifier
                .size(mSize.value.width.dp, mSize.value.height.dp)
                .zIndex(mapZValue)
                .align(if (isLandScape) Alignment.TopStart else Alignment.BottomCenter)
                .padding(if (mainViewModel.mapIsMainScreen || !isLandScape) 0.dp else 15.dp)
            //.clip(RoundedCornerShape(10.dp))
        ) {
            if (!isLandScape) {
                TelemetryInfo(
                    modifier = Modifier
                        .zIndex(mapZValue + 1)
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    distFromHome = distToHome.value,
                    height = heightAboveHome.value,
                    speed = groundSpeed.value,
                    heading = heading.value,
                )
            }
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = props,
                uiSettings = MapUiSettings(zoomControlsEnabled = false),
                onMapClick = { mainViewModel.onUiEvent(ScreenEvent.MapTapped) }
            ) {
                vehiclePosition.value?.let { position ->
                    VehicleMapMarker(
                        context = LocalContext.current,
                        position = LatLng(position.lat.value, position.lon.value),
                        title = "Vehicle",
                        iconResourceId = R.drawable.plane,
                        rotation = attitude.value?.let { it.yaw.toDegrees() } ?: Degrees(),
                    )
                }

                Polyline(points = vehiclePath.value, color = Color.Red)
            }

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
            AndroidView(
                modifier = Modifier
                    .size(width = vSize.value.width.dp, vSize.value.height.dp),
                factory = {
                    StyledPlayerView(it).apply {
                        this.player = player
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    }
                })

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
