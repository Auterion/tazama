package com.auterion.tazama.survey.utils.geo

import com.mapbox.mapboxsdk.geometry.LatLng
import kotlin.math.max

data class BoundingRectangleCorners(
    val topLeft: PointF = PointF(),
    val topRight: PointF = PointF(),
    val bottomRight: PointF = PointF(),
    val bottomLeft: PointF = PointF(),
)

class BoundingRectanglePolygon(vertices: List<PointF>, topLeftOrigin: LatLng) {

    private val _corners = if (vertices.isEmpty()) {
        BoundingRectangleCorners()
    } else {
        BoundingRectangleCorners(
            PointF(vertices.map { it.x }.min(), vertices.map {
                it.y
            }.min()),
            PointF(vertices.map { it.x }.max(), vertices.map {
                it.y
            }.min()),
            PointF(vertices.map { it.x }.max(), vertices.map {
                it.y
            }.max()),
            PointF(vertices.map { it.x }.min(), vertices.map {
                it.y
            }.max())
        )
    }

    val corners get() = _corners
    private val _origin = topLeftOrigin
    val origin get() = _origin

    fun getCenterPoint(): PointF {
        val centerX = (_corners.topRight.x - _corners.topLeft.x) * 0.5f + corners.topLeft.x
        val centerY = (corners.bottomRight.y - corners.topRight.y) * 0.5f + corners.topRight.y

        return PointF(centerX, centerY)
    }

    fun getSquareEnlargedByFactor(factor: Float): BoundingRectangleCorners {
        val center = getCenterPoint()
        val maxDist =
            max(
                corners.topRight.x - corners.topLeft.x,
                corners.bottomRight.y - corners.topRight.y
            ) * factor

        return BoundingRectangleCorners(
            PointF(center.x - maxDist * 0.5f, center.y - maxDist * 0.5f),
            PointF(center.x + maxDist * 0.5f, center.y - maxDist * 0.5f),
            PointF(center.x + maxDist * 0.5f, center.y + maxDist * 0.5f),
            PointF(center.x - maxDist * 0.5f, center.y + maxDist * 0.5f)
        )
    }
}
