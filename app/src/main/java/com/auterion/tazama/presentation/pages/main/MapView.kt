package com.auterion.tazama.presentation.pages.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.auterion.tazama.R
import com.auterion.tazama.data.vehicle.VehicleViewModel
import com.auterion.tazama.presentation.components.VehicleMapMarker
import com.auterion.tazama.presentation.pages.settings.SettingsViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import kotlin.math.max

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

    val mSize = mainViewModel.mapSize.collectAsState()
    val vSize = mainViewModel.videoSize.collectAsState()
    val mapZValue = mainViewModel.mapZValue.collectAsState(initial = 0.0F).value
    
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
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

            if (!mainViewModel.mapIsMainScreen && mainViewModel.showDragIndicators) {
                WindowDragger(onDragAmount = {
                    mainViewModel.onUiEvent(ScreenEvent.MapWindowDrag(it))
                }, modifier = Modifier.align(Alignment.BottomEnd))
            }
        }

        Box(modifier = Modifier
            .background(color = Color.Black)
            .size(vSize.value.width.dp, vSize.value.height.dp)
            .align(Alignment.TopStart)
            .clickable {
                mainViewModel.onUiEvent(ScreenEvent.VideoTapped)
            }) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(max(0.1F, vSize.value.width / max(vSize.value.height, 1.0F))),
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

@Composable
fun WindowDragger(
    onDragAmount: (Offset) -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .background(color = Color.White)
            .size(30.dp)
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    onDragAmount(
                        dragAmount.copy(
                            x = dragAmount.x / density, y = dragAmount.y / density
                        )
                    )

                }
            }
    ) {
        Image(
            painterResource(id = R.drawable.diagonal_arrow),
            contentDescription = "null",
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        )
    }
}
