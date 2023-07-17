package com.auterion.tazama.survey.utils.geo

import kotlin.math.abs
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
            if (start.y < end.y) {
                m = Float.POSITIVE_INFINITY
            } else {
                m = Float.NEGATIVE_INFINITY
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

    fun length(): Float {
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

    fun isVertical(): Boolean {
        return start.x == end.x
    }

    fun pointIsOnLine(point: PointF): Boolean {

        // first attempt to work around numerical issues, see unit tests for Line
        val delta = 0.01 * length()

        if (isVertical()) {
            return abs(point.x - start.x) < 0.001f
        } else {
            return (point.x + 0.001f) >= min(start.x, end.x) && (point.x - 0.001f) <= max(
                start.x,
                end.x
            ) && abs(point.y - (m * point.x + b)) < delta
        }
    }

    fun intersect(other: Line): LineInterSectionPoint? {
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
                return@intersect null
            }
        }

        if (!pointIsOnLine(point) || !other.pointIsOnLine(point)) {
            return null
        }

        return LineInterSectionPoint(point)
    }

    fun toList(): List<PointF> {
        return listOf(start, end)
    }
}

data class LineInterSectionPoint(val point: PointF)
