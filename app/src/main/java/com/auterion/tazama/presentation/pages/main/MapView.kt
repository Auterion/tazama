package com.auterion.tazama.presentation.pages.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.auterion.tazama.R
import com.auterion.tazama.data.vehicle.VehicleViewModel
import com.auterion.tazama.presentation.components.VehicleMapMarker
import com.auterion.tazama.presentation.pages.settings.SettingsViewModel
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*

@Composable
fun MapView(
    vehicleViewModel: VehicleViewModel,
    modifier: Modifier
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

    val videoStreamUri = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4"
    val context = LocalContext.current
    val customLoadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(0, 0, 0, 0)
        .build()
    val player = remember { ExoPlayer.Builder(context).setLoadControl(customLoadControl).build() }
    val mediaSource = RtspMediaSource.Factory()
        .setForceUseRtpTcp(videoStreamUri.contains("rtspt"))
        .createMediaSource(MediaItem.fromUri(videoStreamUri))

    player.setMediaSource(mediaSource)
    player.prepare()
    player.play()

    Box {
        GoogleMap(
            modifier = modifier,
            cameraPositionState = cameraPositionState,
            properties = props,
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
        ) {
            VehicleMapMarker(
                context = LocalContext.current,
                position = vehiclePosition.value,
                title = "Vehicle",
                iconResourceId = R.drawable.drone
            )
        }
        AndroidView(
            modifier = Modifier
                .size(500.dp, 500.dp)
                .align(Alignment.TopEnd),
            factory = {
                StyledPlayerView(it).apply {
                    this.player = player
                    useController = false
                }
            }
        )
    }
}
