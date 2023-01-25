package com.auterion.tazama.data.vehicle

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface Camera {
    val videoStreamInfo: StateFlow<VideoStreamInfo>

}

interface CameraWriter {
    val videoStreamInfoWriter: MutableStateFlow<VideoStreamInfo>
}

class CameraImpl : Camera, CameraWriter {
    override val videoStreamInfoWriter = MutableStateFlow(VideoStreamInfo(""))
    override val videoStreamInfo = videoStreamInfoWriter.asStateFlow()
}

data class VideoStreamInfo(val uri: String)