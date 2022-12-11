package com.auterion.tazama.data.vehicle

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface Telemetry {
    val position: StateFlow<LatLng>
}

interface TelemetryWriter {
    val positionWriter: MutableStateFlow<LatLng>
}

class TelemetryImpl : Telemetry, TelemetryWriter {
    override val positionWriter = MutableStateFlow(LatLng(0.0, 0.0))
    override val position = positionWriter.asStateFlow()
}