/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package com.auterion.tazama.presentation.pages.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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
    private val player: ExoPlayer,
    videoStreamInfo: StateFlow<VideoStreamInfo?>,
) : ViewModel() {
    companion object {
        fun factory(
            player: ExoPlayer,
            videoStreamInfo: StateFlow<VideoStreamInfo?>
        ): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    MainViewModel(player, videoStreamInfo)
                }
            }
        }
    }

    private val _cameraPositionState = mutableStateOf(CameraPositionState())

    init {
        connectVideoStreamInfoFlow(videoStreamInfo)
    }

    val cameraPositionState
        get() = _cameraPositionState.value

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun connectVideoStreamInfoFlow(flow: StateFlow<VideoStreamInfo?>) {
        viewModelScope.launch {
            flow.filterNotNull()
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
