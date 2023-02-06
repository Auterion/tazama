package com.auterion.tazama.data.vehicle

import kotlin.math.PI

interface Angle

data class Radian(val value: Double = 0.0) : Angle {
    fun toDegrees(): Degrees {
        return Degrees(value * 180.0 / PI)
    }
}

data class Degrees(val value: Double = 0.0) : Angle {
    fun toRadian(): Radian {
        return Radian(value * PI / 180.0)
    }

    operator fun minus(other: Degrees): Degrees {
        return Degrees(this.value - other.value)
    }

    operator fun times(other: Double): Degrees {
        return Degrees(this.value * other)
    }
}
