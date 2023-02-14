package com.auterion.tazama.data.vehicle

const val METER_TO_FEET = 3.0

abstract class Measure<T : Measure<T>>(val measurementSystem: MeasurementSystem = MeasurementSystem.METRIC) {
    fun toSystem(system: MeasurementSystem): T {
        return when (system) {
            MeasurementSystem.METRIC -> toMetric()
            MeasurementSystem.IMPERIAL -> toImperial()
        }
    }

    abstract fun toMetric(): T
    abstract fun toImperial(): T

    enum class MeasurementSystem { METRIC, IMPERIAL }
}

class Speed(
    val value: Double = 0.0,
    measurementSystem: MeasurementSystem = MeasurementSystem.METRIC
) : Measure<Speed>(measurementSystem) {
    val unit: String
        get() = when (measurementSystem) {
            MeasurementSystem.METRIC -> "m/s"
            MeasurementSystem.IMPERIAL -> "f/s"
        }

    override fun toMetric(): Speed {
        return Speed(value / METER_TO_FEET)
    }

    override fun toImperial(): Speed {
        return Speed(value * METER_TO_FEET, MeasurementSystem.IMPERIAL)
    }
}

class Distance(
    val value: Double = 0.0,
    measurementSystem: MeasurementSystem = MeasurementSystem.METRIC
) : Measure<Distance>(measurementSystem) {
    val unit: String
        get() = when (measurementSystem) {
            MeasurementSystem.METRIC -> "m"
            MeasurementSystem.IMPERIAL -> "f"
        }

    override fun toMetric(): Distance {
        return Distance(value / METER_TO_FEET)
    }

    override fun toImperial(): Distance {
        return Distance(value * METER_TO_FEET, MeasurementSystem.IMPERIAL)
    }
}

class Altitude(
    val value: Double = 0.0,
    measurementSystem: MeasurementSystem = MeasurementSystem.METRIC
) : Measure<Altitude>(measurementSystem) {
    val unit: String
        get() = when (measurementSystem) {
            MeasurementSystem.METRIC -> "m"
            MeasurementSystem.IMPERIAL -> "f"
        }

    override fun toMetric(): Altitude {
        return Altitude(value / METER_TO_FEET)
    }

    override fun toImperial(): Altitude {
        return Altitude(value * METER_TO_FEET, MeasurementSystem.IMPERIAL)
    }

    operator fun minus(other: Altitude): Altitude {
        return Altitude(this.value - other.value)
    }
}
