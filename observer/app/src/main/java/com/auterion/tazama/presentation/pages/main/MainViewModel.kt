package com.auterion.tazama.presentation.pages.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auterion.tazama.libvehicle.PositionAbsolute
import com.auterion.tazama.libvehicle.VideoStreamInfo
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val player: ExoPlayer
) : ViewModel() {
    private var videoStreamInfo: StateFlow<VideoStreamInfo?>? = null

    private val _cameraPositionState = mutableStateOf(CameraPositionState())
    val cameraPositionState
        get() = _cameraPositionState.value

    fun setVideoStreamInfoFlow(flow: StateFlow<VideoStreamInfo?>) {
        if (videoStreamInfo != null) {
            return
        }
        videoStreamInfo = flow

        viewModelScope.launch {
            videoStreamInfo!!
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
