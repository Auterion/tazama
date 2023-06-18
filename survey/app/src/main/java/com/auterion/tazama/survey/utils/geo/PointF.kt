package com.auterion.tazama.survey.utils.geo

import kotlin.math.cos
import kotlin.math.sin

class PointF(var x: Float = 0.0f, var y: Float = 0.0f) {
    operator fun minus(other: PointF): PointF {
        return PointF(other.x - x, other.y - y)
    }

    operator fun plus(other: PointF): PointF {
        return PointF(x + other.x, y + other.y)
    }
}

fun PointF.rotateAroundCenter(center: PointF, angle: Double): PointF {
    val tmp = this - center
    val newStartX = cos(angle) * tmp.x - sin(angle) * tmp.y
    val newStartY = cos(angle) * tmp.y + sin(angle) * tmp.x

    return PointF(newStartX.toFloat(), newStartY.toFloat()) + center
}
