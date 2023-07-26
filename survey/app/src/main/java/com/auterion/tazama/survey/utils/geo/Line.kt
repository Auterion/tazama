package com.auterion.tazama.survey.utils.geo

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class Line(start: PointF, end: PointF) {
    val start: PointF
    val end: PointF

    val m: Float
    val b: Float

    init {
        var startMod = start

        if (abs(end.x - start.x) < 0.001f) {
            m = if (start.y < end.y) {
                Float.POSITIVE_INFINITY
            } else {
                Float.NEGATIVE_INFINITY
            }
            b = 0.0f
            startMod = start.copy(x = end.x)
        } else {
            m = (end.y - start.y) / (end.x - start.x)
            b = start.y - m * start.x
        }

        this.start = startMod
        this.end = end
    }

    private fun length(): Float {
        val tmp = end - start
        return sqrt(tmp.x * tmp.x + tmp.y * tmp.y)
    }

    fun rotateAroundCenter(center: PointF, angle: Double): Line {
        val newStartX = start.rotateAroundCenter(center, angle)
        val newStartY = end.rotateAroundCenter(center, angle)

        return Line(
            newStartX,
            newStartY
        )
    }

    private fun isVertical(): Boolean {
        return start.x == end.x
    }

    fun pointIsOnLine(point: PointF): Boolean {
        // first attempt to work around numerical issues, see unit tests for Line
        val delta = 0.01 * length()

        return if (isVertical()) {
            abs(point.x - start.x) < 0.001f
        } else {
            (point.x + 0.001f) >= min(start.x, end.x) && (point.x - 0.001f) <= max(
                start.x,
                end.x
            ) && abs(point.y - (m * point.x + b)) < delta
        }
    }

    fun intersect(other: Line): LineIntersectionPoint? {
        val point: PointF = when {
            other.isVertical() && !isVertical() -> {
                val y = m * other.start.x + b
                val x = other.start.x
                PointF(x, y)
            }

            !other.isVertical() && isVertical() -> {
                val y = other.m * start.x + other.b
                val x = start.x
                PointF(x, y)
            }

            abs(other.m) != abs(m) && !other.isVertical() && !isVertical() -> {
                val x = (other.b - b) / (m - other.m)
                val y = m * x + b

                PointF(x, y)
            }

            else -> {
                return null
            }
        }

        if (!pointIsOnLine(point) || !other.pointIsOnLine(point)) {
            return null
        }

        return LineIntersectionPoint(point)
    }

    fun getNormalizedDirection(): PointF {
        return (end - start).normalized()
    }

    fun getLength(): Float {
        val tmp = end - start
        return sqrt(tmp.x * tmp.x + tmp.y * tmp.y)
    }

    fun getMidPoint(): PointF {
        return start + getNormalizedDirection() * length() * 0.5f
    }

    fun getAzimuth(): Float {
        val tmp = getNormalizedDirection()
        return wrapPi(atan2(tmp.y, tmp.x))
    }

    fun wrapPi(value: Float): Float {
        var tmp = value
        while (tmp > PI.toFloat()) {
            tmp -= 2 * PI.toFloat()
        }

        while (tmp < -PI.toFloat()) {
            tmp += 2 * PI.toFloat()
        }

        return tmp
    }
}

data class LineIntersectionPoint(val point: PointF)
