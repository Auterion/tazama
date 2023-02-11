package com.auterion.tazama.data.vehicle

const val METER_TO_FEET = 3.0

abstract class Measure(
    val value: Double,
    val measurementSystem: MeasurementSystem = MeasurementSystem.METRIC
) {
    abstract val unit: String
    abstract fun toMetric(): Measure
    abstract fun toImperial(): Measure

    enum class MeasurementSystem { METRIC, IMPERIAL }
}

class Speed(value: Double = 0.0, measurementSystem: MeasurementSystem = MeasurementSystem.METRIC) :
    Measure(value, measurementSystem) {
    override val unit: String
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
    value: Double = 0.0,
    measurementSystem: MeasurementSystem = MeasurementSystem.METRIC
) : Measure(value, measurementSystem) {
    override val unit: String
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
    value: Double = 0.0,
    measurementSystem: MeasurementSystem = MeasurementSystem.METRIC
) : Measure(value, measurementSystem) {
    override val unit: String
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
