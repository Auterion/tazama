package com.auterion.tazama.data.vehicle

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.PI

data class Radian(val value: Double) {
    fun toDegrees(): Double {
        return value * 180.0 / PI
    }
}

data class VelocityNed(val vx: Double, val vy: Double, val vz: Double)
data class Euler(val roll: Radian, val pitch: Radian, val yaw: Radian)

interface Telemetry {
    val position: StateFlow<LatLng>
    val velocity: StateFlow<VelocityNed>
    val attitude: StateFlow<Euler>
}

interface TelemetryWriter {
    val positionWriter: MutableStateFlow<LatLng>
    val velocityWriter: MutableStateFlow<VelocityNed>
    val attitudeWriter: MutableStateFlow<Euler>
}

class TelemetryImpl : Telemetry, TelemetryWriter {
    override val positionWriter = MutableStateFlow(LatLng(0.0, 0.0))
    override val velocityWriter = MutableStateFlow(VelocityNed(0.0, 0.0, 0.0))
    override val attitudeWriter = MutableStateFlow(Euler(Radian(0.0), Radian(0.0), Radian(0.0)))
    override val position = positionWriter.asStateFlow()
    override val velocity = velocityWriter.asStateFlow()
    override val attitude = attitudeWriter.asStateFlow()

}