package com.auterion.tazama.presentation.pages.main

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class MainViewModel @Inject constructor(
    val player: ExoPlayer
) : ViewModel() {
    private var videoStreamInfo: StateFlow<VideoStreamInfo?>? = null

    private val _videoSize = MutableStateFlow(Size(0.0F, 0.0F))
    val videoSize = _videoSize.asStateFlow()

    private val videoWidthToHeightRatio: Float = 9.0f / 16.0f

    private val _mapSize = MutableStateFlow(Size(0.0F, 0.0F))
    val mapSize = _mapSize.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val mapZValue = snapshotFlow { _mapIsMainScreen.value }.mapLatest {
        if (it) {
            0.0F
        } else {
            1.0F
        }
    }

    private val screenSize = mutableStateOf(Size(0.0F, 0.0F))

    @OptIn(ExperimentalCoroutinesApi::class)
    val isLandScape = snapshotFlow { screenSize }.mapLatest {
        isLandScape()
    }

    private val _showDragIndicators = mutableStateOf(false)
    val showDragIndicators
        get() = _showDragIndicators.value

    private val _mapIsMainScreen = mutableStateOf(true)
    val mapIsMainScreen
        get() = _mapIsMainScreen.value

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

    private fun swapMapAndVideo() {
        _mapIsMainScreen.value = !_mapIsMainScreen.value

        if (_mapIsMainScreen.value) {
            _videoSize.value =
                Size(_mapSize.value.width, videoWidthToHeightRatio * _mapSize.value.width)
            _mapSize.value = screenSize.value
        } else {
            _mapSize.value = _videoSize.value
            _videoSize.value = screenSize.value
        }
    }

    private fun isLandScape(): Boolean {
        return screenSize.value.width > screenSize.value.height
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

    fun onUiEvent(event: ScreenEvent) {
        when (event) {
            is ScreenEvent.ScreenSizeChanged -> {
                screenSize.value = event.size
                _mapIsMainScreen.value = true
                if (isLandScape()) {
                    _mapSize.value = screenSize.value
                    _showDragIndicators.value = true
                    _mapSize.value = screenSize.value
                    val desiredVideoHeight = _mapSize.value.height * 0.5f

                    _videoSize.value = Size(
                        desiredVideoHeight / videoWidthToHeightRatio,
                        desiredVideoHeight
                    )
                } else {
                    _showDragIndicators.value = false
                    _videoSize.value = Size(
                        screenSize.value.width,
                        screenSize.value.width * videoWidthToHeightRatio
                    )
                    _mapSize.value = Size(
                        screenSize.value.width,
                        screenSize.value.height - _videoSize.value.height
                    )
                }
            }

            is ScreenEvent.VideoWindowDrag -> {
                if (_mapIsMainScreen.value && isLandScape()) {
                    val newVideoWidth = _videoSize.value.width + event.drag.x
                    val newVideoHeight = newVideoWidth * videoWidthToHeightRatio

                    val limit = min(screenSize.value.width, screenSize.value.height)

                    if (newVideoHeight < limit ||
                        newVideoWidth < limit
                    ) {
                        _videoSize.value =
                            Size(newVideoWidth, newVideoHeight)
                    }
                }
            }

            is ScreenEvent.MapWindowDrag -> {
                if (!_mapIsMainScreen.value && isLandScape()) {
                    val newMapWidth = _mapSize.value.width + event.drag.x
                    val newMapHeight = newMapWidth * videoWidthToHeightRatio
                    val limit = min(screenSize.value.width, screenSize.value.height)
                    if (isLandScape() && newMapHeight < limit ||
                        !isLandScape() && newMapWidth < limit
                    ) {
                        _mapSize.value =
                            Size(newMapWidth, newMapWidth * videoWidthToHeightRatio)
                    }
                }
            }

            is ScreenEvent.MapTapped -> {
                if (isLandScape() && !_mapIsMainScreen.value) {
                    swapMapAndVideo()
                }
            }

            is ScreenEvent.VideoTapped -> {
                if (isLandScape() && _mapIsMainScreen.value) {
                    swapMapAndVideo()
                }
            }

            is ScreenEvent.CenterVehicle -> {
            }
        }
    }
}

sealed class ScreenEvent {
    data class ScreenSizeChanged(val size: Size) : ScreenEvent()
    data class VideoWindowDrag(val drag: Offset) : ScreenEvent()
    data class MapWindowDrag(val drag: Offset) : ScreenEvent()
    object VideoTapped : ScreenEvent()
    object MapTapped : ScreenEvent()
    object CenterVehicle : ScreenEvent()
}
