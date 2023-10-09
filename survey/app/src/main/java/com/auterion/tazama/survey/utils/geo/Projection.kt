/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package com.auterion.tazama.survey.utils.geo

import com.mapbox.mapboxsdk.geometry.LatLng
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class LocalProjection(private val origin: LatLng) {

    private val refLat = this.origin.latitude * Math.PI / 180
    private val refLon = this.origin.longitude * Math.PI / 180

    private val refSinLat = Math.sin(refLat)
    private val refCosLat = Math.cos(refLat)

    private val radiusEarth = 6371000


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
        val cosDLon = Math.cos(lonRad - refLon)
        var arg = refSinLat * sinLat + refCosLat * cosLat * cosDLon
        arg = max(min(1.0, arg), -1.0)

        val c = Math.acos(arg)

        var k = 1.0;

        if (abs(c) > 0) {
            k = (c / Math.sin(c))
        }

        val x = k * (refCosLat * sinLat - refSinLat * cosLat * cosDLon) * radiusEarth
        val y = k * cosLat * Math.sin(lonRad - refLon) * radiusEarth

        // HACK: Order was changed to continue working in screen coordinates, should not be here
        return PointF(y.toFloat(), -x.toFloat())
    }

    fun reproject(point: PointF): LatLng {
        // HACK: Order was changed to continue working in screen coordinates, should not be here
        val xRad = (-point.y / radiusEarth).toDouble()
        val yRad = (point.x / radiusEarth).toDouble()
        val c = Math.sqrt(xRad * xRad + yRad * yRad)

        if (abs(c) > 0) {
            val sinC = Math.sin(c)
            val cosC = Math.cos(c)
            val latRad = Math.asin(cosC * refSinLat + (xRad * sinC * refCosLat) / c)
            val lonRad = (refLon + Math.atan2(
                yRad * sinC,
                c * refCosLat * cosC - xRad * refSinLat * sinC
            ))


            return LatLng(degrees(latRad), degrees(lonRad))

        } else {
            return LatLng(degrees(refLat), degrees(refLon))
        }
    }
}
