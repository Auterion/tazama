package com.auterion.tazama.presentation.pages.main

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.math.min

class MainViewModel @Inject constructor() : ViewModel() {
    private val _mapSize = MutableStateFlow<Size>(Size(0.0F, 0.0F))
    val mapSize = _mapSize.asStateFlow()

    private val _videoSize = MutableStateFlow<Size>(Size(0.0F, 0.0F))
    val videoSize = _videoSize.asStateFlow()

    val screenSize = mutableStateOf(Size(0.0F, 0.0F))

    private val _videoWidthToHeightRatio: Float = 480.0f / 640.0f   // TODO make this variable

    private val _mapIsMainScreen = mutableStateOf(true)
    val mapisMainScreen = _mapIsMainScreen

    fun swapMapAndVideo() {
        if (_mapIsMainScreen.value) {
            _videoSize.value =
                Size(_mapSize.value.width, _videoWidthToHeightRatio * _mapSize.value.width)
            _mapSize.value = screenSize.value
        } else {
            _mapSize.value = _videoSize.value
            _videoSize.value = screenSize.value
        }
    }

    fun isLandScape(): Boolean {
        return screenSize.value.width > screenSize.value.height
    }

    fun onUiEvent(event: ScreenEvent) {
        when (event) {
            is ScreenEvent.ScreenSizeChanged -> {
                screenSize.value = event.size
                _mapSize.value = screenSize.value
                _mapIsMainScreen.value = true
                if (isLandScape()) {
                    _videoSize.value = Size(
                        _mapSize.value.height / _videoWidthToHeightRatio,
                        _mapSize.value.height
                    )
                } else {
                    _videoSize.value =
                        Size(_mapSize.value.width, _mapSize.value.width * _videoWidthToHeightRatio)
                }
            }

            is ScreenEvent.VideoWindowDrag -> {
                if (_mapIsMainScreen.value) {
                    val newVideoWidth = _videoSize.value.width + event.drag.y
                    val newVideoHeight = newVideoWidth * _videoWidthToHeightRatio

                    val limit = min(screenSize.value.width, screenSize.value.height)

                    if (isLandScape() && newVideoHeight < limit ||
                        !isLandScape() && newVideoWidth < limit
                    ) {
                        _videoSize.value =
                            Size(newVideoWidth, newVideoHeight)
                    }
                }
            }

            is ScreenEvent.MapWindowDrag -> {
                if (!_mapIsMainScreen.value) {
                    val newMapWidth = _mapSize.value.width + event.drag.y
                    val newMapHeight = newMapWidth * _videoWidthToHeightRatio
                    val limit = min(screenSize.value.width, screenSize.value.height)
                    if (isLandScape() && newMapHeight < limit ||
                        !isLandScape() && newMapWidth < limit
                    ) {
                        _mapSize.value = Size(newMapWidth, newMapWidth * _videoWidthToHeightRatio)
                    }
                }
            }

            is ScreenEvent.MapTapped -> {
                if (!_mapIsMainScreen.value) {
                    _mapIsMainScreen.value = !_mapIsMainScreen.value
                    swapMapAndVideo()
                }
            }

            is ScreenEvent.VideoTapped -> {
                if (_mapIsMainScreen.value) {
                    _mapIsMainScreen.value = !_mapIsMainScreen.value
                    swapMapAndVideo()
                }
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
}