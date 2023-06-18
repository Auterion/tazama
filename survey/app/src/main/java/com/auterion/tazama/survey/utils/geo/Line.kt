package com.auterion.tazama.survey.utils.geo

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class Line(start: PointF, end: PointF) {
    private val _start = start
    val start get() = _start
    private val _end = end
    val end get() = _end

    private var _m: Float = 0.0f
    val m get() = _m
    private var _b: Float = 0.0f
    val b get() = _b

    init {

        if (abs(_end.x - _start.x) < 0.001f) {
            if (_start.y < _end.y) {
                _m = Float.POSITIVE_INFINITY
            } else {
                _m = Float.NEGATIVE_INFINITY
            }
            _start.x = _end.x
        } else {
            _m = (_end.y - _start.y) / (_end.x - _start.x)
            _b = _start.y - _m * _start.x
        }
    }

    fun length(): Float {
        val tmp = _end - start
        return sqrt(tmp.x * tmp.x + tmp.y * tmp.y)
    }

    fun rotateAroundCenter(center: PointF, angle: Double): Line {
        val newStartX = _start.rotateAroundCenter(center, angle)
        val newStartY = _end.rotateAroundCenter(center, angle)

        return Line(
            newStartX,
            newStartY
        )
    }

    fun isVertical(): Boolean {
        return _start.x == _end.x
    }

    fun pointIsOnLine(point: PointF): Boolean {

        // first attempt to work around numerical issues, see unit tests for Line
        val delta = 0.01 * length()

        if (isVertical()) {
            return abs(point.x - _start.x) < 0.001f
        } else {
            return (point.x + 0.001f) >= min(_start.x, _end.x) && (point.x - 0.001f) <= max(
                _start.x,
                _end.x
            ) && abs(point.y - (m * point.x + b)) < delta
        }
    }

    fun intersect(other: Line): LineInterSectionPoint? {
        val point: PointF? = when {
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
                null
            }
        }

        point?.let {
            if (pointIsOnLine(it) && other.pointIsOnLine(it)) {
                return LineInterSectionPoint(it)
            }
        }

        return null

    }

    fun toList(): List<PointF> {
        return listOf(_start, _end)
    }
}

data class LineInterSectionPoint(val point: PointF)
