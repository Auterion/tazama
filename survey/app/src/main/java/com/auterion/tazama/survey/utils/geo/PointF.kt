package com.auterion.tazama.survey.utils.geo

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class PointF(val x: Float = 0.0f, val y: Float = 0.0f) {
    operator fun minus(other: PointF): PointF {
        return PointF(x - other.x, y - other.y)
    }

    operator fun plus(other: PointF): PointF {
        return PointF(x + other.x, y + other.y)
    }

    fun normalized(): PointF {
        val length = sqrt(x * x + y * y)
        return if (length > 0.0f) {
            PointF(x / length, y / length)
        } else {
            return PointF()
        }
    }

    operator fun times(factor: Float): PointF {
        return PointF(x * factor, y * factor)
    }
}

fun PointF.rotateAroundCenter(center: PointF, angle: Double): PointF {
    val tmp = this - center
    val newStartX = cos(angle) * tmp.x - sin(angle) * tmp.y
    val newStartY = cos(angle) * tmp.y + sin(angle) * tmp.x

    return PointF(newStartX.toFloat(), newStartY.toFloat()) + center
}
