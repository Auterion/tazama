package com.auterion.tazama.data.vehicle

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
        TODO("Not yet implemented")
    }

    override fun toImperial(): Speed {
        return Speed(value * 3, MeasurementSystem.IMPERIAL)
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
        TODO("Not yet implemented")
    }

    override fun toImperial(): Distance {
        return Distance(value * 3, MeasurementSystem.IMPERIAL)
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
        TODO("Not yet implemented")
    }

    override fun toImperial(): Altitude {
        return Altitude(value * 3, MeasurementSystem.IMPERIAL)
    }

    operator fun minus(other: Altitude): Altitude {
        return Altitude(this.value - other.value)
    }
}
