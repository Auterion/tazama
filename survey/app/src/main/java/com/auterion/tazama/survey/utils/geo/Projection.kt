package com.auterion.tazama.survey.utils.geo

import com.mapbox.mapboxsdk.geometry.LatLng
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class LocalProjection(origin: LatLng) {
    private val _origin = origin

    private val _refLat = _origin.latitude * Math.PI / 180
    private val _refLon = _origin.longitude * Math.PI / 180

    private val _refSinLat = Math.sin(_refLat)
    private val _refCosLat = Math.cos(_refLat)

    private val _radiusEarth = 6371000


    fun radians(degrees: Double): Double {
        return degrees * Math.PI / 180
    }

    fun degrees(radians: Double): Double {
        return radians * 180 / Math.PI
    }

    fun project(coord: LatLng): PointF {
        val latRad = radians(coord.latitude)
        val lonRad = radians(coord.longitude)

        val sinLat = Math.sin(latRad)
        val cosLat = Math.cos(latRad)
        val cosDLon = Math.cos(lonRad - _refLon)
        var arg = _refSinLat * sinLat + _refCosLat * cosLat * cosDLon
        arg = max(min(1.0, arg), -1.0)

        val c = Math.acos(arg)

        var k = 1.0;

        if (abs(c) > 0) {
            k = (c / Math.sin(c))
        }

        val x = k * (_refCosLat * sinLat - _refSinLat * cosLat * cosDLon) * _radiusEarth
        val y = k * cosLat * Math.sin(lonRad - _refLon) * _radiusEarth

        // HACK: Order was changed to continue working in screen coordinates, should not be here
        return PointF(y.toFloat(), -x.toFloat())
    }

    fun reproject(point: PointF): LatLng {
        // HACK: Order was changed to continue working in screen coordinates, should not be here
        val xRad = (-point.y / _radiusEarth).toDouble()
        val yRad = (point.x / _radiusEarth).toDouble()
        val c = Math.sqrt(xRad * xRad + yRad * yRad)

        if (abs(c) > 0) {
            val sinC = Math.sin(c)
            val cosC = Math.cos(c)
            val latRad = Math.asin(cosC * _refSinLat + (xRad * sinC * _refCosLat) / c)
            val lonRad = (_refLon + Math.atan2(
                yRad * sinC,
                c * _refCosLat * cosC - xRad * _refSinLat * sinC
            ))


            return LatLng(degrees(latRad), degrees(lonRad))

        } else {
            return LatLng(degrees(_refLat), degrees(_refLon))
        }
    }
}
