package com.auterion.tazama.presentation.pages.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import com.auterion.tazama.libvehicle.PositionAbsolute
import com.auterion.tazama.libvehicle.VideoStreamInfo
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class MainViewModel(
    val player: ExoPlayer,
    private val videoStreamInfo: StateFlow<VideoStreamInfo?>,
) : ViewModel() {
    private val _cameraPositionState = mutableStateOf(CameraPositionState())

    init {
        connectVideoStreamInfoFlow(videoStreamInfo)
    }

    val cameraPositionState
        get() = _cameraPositionState.value

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun connectVideoStreamInfoFlow(flow: StateFlow<VideoStreamInfo?>) {
        viewModelScope.launch {
            videoStreamInfo
                .filterNotNull()
                .distinctUntilChanged { left, right -> left.uri == right.uri }
                .collect {
                    player.stop()
                    val mediaSource = RtspMediaSource.Factory()
                        .setForceUseRtpTcp(it.uri.contains("rtspt"))
                        .createMediaSource(MediaItem.fromUri(it.uri))

                    player.setMediaSource(mediaSource)
                    player.prepare()
                    player.play()
                }
        }
    }

    fun centerOnPosition(position: PositionAbsolute) {
        _cameraPositionState.value = CameraPositionState(
            position = CameraPosition(
                LatLng(
                    position.lat.value,
                    position.lon.value
                ),
                cameraPositionState.position.zoom,
                cameraPositionState.position.tilt,
                cameraPositionState.position.bearing
            )
        )
    }
}
