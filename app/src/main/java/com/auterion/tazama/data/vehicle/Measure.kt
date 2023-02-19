package com.auterion.tazama.data.vehicle

const val METER_TO_FEET = 3.2808398950131

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
            MeasurementSystem.IMPERIAL -> "ft/s"
        }

    override fun toMetric(): Speed {
        return when (measurementSystem) {
            MeasurementSystem.METRIC -> this
            else -> return Speed(value / METER_TO_FEET)
        }
    }

    override fun toImperial(): Speed {
        return when (measurementSystem) {
            MeasurementSystem.METRIC -> Speed(value * METER_TO_FEET, MeasurementSystem.IMPERIAL)
            MeasurementSystem.IMPERIAL -> this
        }
    }

    override fun equals(other: Any?) =
        other is Speed && other.measurementSystem == measurementSystem && other.value == value

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + measurementSystem.hashCode()
        return result
    }
}

class Distance(
    val value: Double = 0.0,
    measurementSystem: MeasurementSystem = MeasurementSystem.METRIC
) : Measure<Distance>(measurementSystem) {
    val unit: String
        get() = when (measurementSystem) {
            MeasurementSystem.METRIC -> "m"
            MeasurementSystem.IMPERIAL -> "ft"
        }

    override fun toMetric(): Distance {
        return when (measurementSystem) {
            MeasurementSystem.METRIC -> this
            MeasurementSystem.IMPERIAL -> Distance(value / METER_TO_FEET)
        }
    }

    override fun toImperial(): Distance {
        return when (measurementSystem) {
            MeasurementSystem.METRIC -> Distance(value * METER_TO_FEET, MeasurementSystem.IMPERIAL)
            MeasurementSystem.IMPERIAL -> this
        }
    }

    operator fun compareTo(other: Distance): Int {
        if ((this.value - other.value) > 0) {
            return 1
        } else if ((this.value - other.value) < 0) {
            return -1
        } else {
            return 0
        }
    }

    override fun equals(other: Any?) =
        other is Distance && other.measurementSystem == measurementSystem && other.value == value

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + measurementSystem.hashCode()
        return result
    }
}

class Altitude(
    val value: Double = 0.0,
    measurementSystem: MeasurementSystem = MeasurementSystem.METRIC
) : Measure<Altitude>(measurementSystem) {
    val unit: String
        get() = when (measurementSystem) {
            MeasurementSystem.METRIC -> "m"
            MeasurementSystem.IMPERIAL -> "ft"
        }

    override fun toMetric(): Altitude {
        return when (measurementSystem) {
            MeasurementSystem.METRIC -> this
            MeasurementSystem.IMPERIAL -> Altitude(value / METER_TO_FEET)
        }
    }

    override fun toImperial(): Altitude {
        return when (measurementSystem) {
            MeasurementSystem.METRIC -> Altitude(
                value * METER_TO_FEET,
                MeasurementSystem.IMPERIAL
            )
            MeasurementSystem.IMPERIAL -> this
        }
    }

    operator fun minus(other: Altitude): Altitude {
        return Altitude(this.value - other.value)
    }
}
