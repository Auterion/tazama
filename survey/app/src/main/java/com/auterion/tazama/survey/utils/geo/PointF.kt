package com.auterion.tazama.survey.utils.geo

import kotlin.math.cos
import kotlin.math.sin

class PointF(var x: Float, var y: Float) {
    operator fun minus(center: PointF): PointF {
        return PointF(x - center.x, y - center.y)
    }
}

fun PointF.rotate(angle: Double): PointF {
    val newStartX = cos(angle) * x - sin(angle) * y
    val newStartY = cos(angle) * y + sin(angle) * x

    return PointF(newStartX.toFloat(), newStartY.toFloat())
}
