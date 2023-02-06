package com.auterion.tazama.data.vehicle

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PositionAbsolute(
    val lat: Degrees = Degrees(),
    val lon: Degrees = Degrees(),
    val alt: Altitude = Altitude(0.0)
) {
    fun toMetric(): PositionAbsolute {
        return if (alt.measurementSystem == Measure.MeasurementSystem.METRIC) {
            this
        } else PositionAbsolute(lat, lon, alt.toMetric())
    }

    fun toImperial(): PositionAbsolute {
        return if (alt.measurementSystem == Measure.MeasurementSystem.IMPERIAL) {
            this
        } else PositionAbsolute(lat, lon, alt.toImperial())
    }
}

data class HomePosition(
    val lat: Degrees? = null,
    val lon: Degrees? = null,
    val alt: Altitude? = null
) {
    fun isValid(): Boolean {
        return lat != Degrees() && lon != Degrees() && alt != null
    }
}

data class VelocityNed(
    val vx: Speed = Speed(),
    val vy: Speed = Speed(),
    val vz: Speed = Speed()
)

data class Euler(
    val roll: Radian = Radian(),
    val pitch: Radian = Radian(),
    val yaw: Radian = Radian()
)

interface Telemetry {
    val position: StateFlow<PositionAbsolute>
    val velocity: StateFlow<VelocityNed>
    val attitude: StateFlow<Euler>
    val homePosition: StateFlow<HomePosition>
}

interface TelemetryWriter {
    val positionWriter: MutableStateFlow<PositionAbsolute>
    val velocityWriter: MutableStateFlow<VelocityNed>
    val attitudeWriter: MutableStateFlow<Euler>
    val homePositionWriter: MutableStateFlow<HomePosition>
}

class TelemetryImpl : Telemetry, TelemetryWriter {
    override val positionWriter = MutableStateFlow(PositionAbsolute())
    override val velocityWriter = MutableStateFlow(VelocityNed())
    override val attitudeWriter = MutableStateFlow(Euler())
    override val homePositionWriter = MutableStateFlow(HomePosition())
    override val position = positionWriter.asStateFlow()
    override val velocity = velocityWriter.asStateFlow()
    override val attitude = attitudeWriter.asStateFlow()
    override val homePosition = homePositionWriter.asStateFlow()
}
