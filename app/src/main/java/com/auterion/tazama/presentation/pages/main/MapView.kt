package com.auterion.tazama.presentation.pages.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.auterion.tazama.R
import com.auterion.tazama.data.vehicle.VehicleViewModel
import com.auterion.tazama.presentation.components.VehicleMapMarker
import com.auterion.tazama.presentation.pages.settings.SettingsViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MapView(
    mainViewModel: MainViewModel,
    vehicleViewModel: VehicleViewModel,
    player: ExoPlayer
) {
    val settingsViewModel = hiltViewModel<SettingsViewModel>()
    val mapType = settingsViewModel.currentMapType.collectAsState()

    val vehiclePosition = vehicleViewModel.vehiclePosition.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(vehiclePosition.value, 10f)
    }

    val props =
        MapProperties(
            mapType = when (mapType.value) {
                "Satellite" -> MapType.SATELLITE
                "Normal" -> MapType.NORMAL
                "Hybrid" -> MapType.HYBRID
                else -> MapType.NORMAL
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

    var mapisMainScreen by mainViewModel.mapisMainScreen

    val mSize = mainViewModel.mapSize.collectAsState()
    val vSize = mainViewModel.videoSize.collectAsState()

    var mapZValue = remember(key1 = mapisMainScreen) {
        if (mapisMainScreen) {
            0.0F
        } else {
            1.0F
        }
    }

    val resources = LocalContext.current.resources
    val density = resources.displayMetrics.density

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .background(Color.Red)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        mainViewModel.onUiEvent(
                            ScreenEvent.MapWindowDrag(
                                dragAmount.copy(
                                    x = dragAmount.x / density, y = dragAmount.y / density
                                )
                            )
                        )
                    }
                }
                .size(mSize.value.width.dp, mSize.value.height.dp)
                .zIndex(mapZValue)
        ) {
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                cameraPositionState = cameraPositionState,
                properties = props,
                uiSettings = MapUiSettings(zoomControlsEnabled = false),
                onMapClick = {
                    mainViewModel.onUiEvent(ScreenEvent.MapTapped)
                }
            ) {
                VehicleMapMarker(
                    context = LocalContext.current,
                    position = vehiclePosition.value,
                    title = "Vehicle",
                    iconResourceId = R.drawable.drone
                )
            }
        }

        Box(modifier = Modifier
            .background(color = Color.Black)
            .pointerInput(Unit) {

                detectDragGestures { change, dragAmount ->
                    mainViewModel.onUiEvent(
                        ScreenEvent.VideoWindowDrag(
                            dragAmount.copy(
                                x = dragAmount.x / density, y = dragAmount.y / density
                            )
                        )
                    )
                }
            }
            .size(vSize.value.width.dp, vSize.value.height.dp)
            .align(Alignment.TopStart)
            .clickable {
                mainViewModel.onUiEvent(ScreenEvent.VideoTapped)
            }) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize(),
                factory = {
                    StyledPlayerView(it).apply {
                        this.player = player
                        useController = false
                    }
                })
        }
    }
}
