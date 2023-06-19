package com.auterion.tazama.survey

import com.auterion.tazama.survey.utils.geo.BoundingRectangleCorners
import com.auterion.tazama.survey.utils.geo.BoundingRectanglePolygon
import com.auterion.tazama.survey.utils.geo.PointF
import com.mapbox.mapboxsdk.geometry.LatLng
import junit.framework.TestCase.assertEquals
import org.junit.Test

class BoundingRectanglePolygonTest {

    // triangle
    val _verticesTriangle = listOf(
        PointF(-1.0f, 0.0f), PointF(1.0f, 0.0f), PointF(0.0f, 1.0f)
    )

    val _boundRectCornersForTriangle = BoundingRectangleCorners(
        topLeft = PointF(-1.0f, 0.0f),
        topRight = PointF(1.0f, 0.0f),
        bottomLeft = PointF(-1.0f, 1.0f),
        bottomRight = PointF(1.0f, 1.0f)
    )

    val _centerForBoundRectForTriangle = PointF(0.0f, 0.5f)

    fun arePointsEqual(a: PointF, b: PointF): Boolean {
        return a.x == b.x && a.y == b.y
    }

    @Test
    fun cornersAreCorrectForUnorderedSquare() {

        // coordinate system is aligned with screen, x points from left to right
        // y points from top to bottom

        val vertices = listOf<PointF>(
            PointF(1.0f, -1.0f), // top right
            PointF(-1.0f, 1.0f), // bottom left
            PointF(-1.0f, -1.0f), // top left
            PointF(1.0f, 1.0f) // bottom right
        )

        val boundRect = BoundingRectanglePolygon(vertices, LatLng())
        val corners = boundRect.corners

        assertEquals(arePointsEqual(corners.topLeft, vertices[2]), true)
        assertEquals(arePointsEqual(corners.topRight, vertices[0]), true)
        assertEquals(arePointsEqual(corners.bottomLeft, vertices[1]), true)
        assertEquals(arePointsEqual(corners.bottomRight, vertices[3]), true)

    }

    @Test
    fun cornersAreCorrectForTriangle() {


        val boundRect = BoundingRectanglePolygon(_verticesTriangle, LatLng())
        val corners = boundRect.corners

        assertEquals(arePointsEqual(corners.topLeft, _boundRectCornersForTriangle.topLeft), true)
        assertEquals(arePointsEqual(corners.topRight, _boundRectCornersForTriangle.topRight), true)
        assertEquals(
            arePointsEqual(corners.bottomLeft, _boundRectCornersForTriangle.bottomLeft),
            true
        )
        assertEquals(
            arePointsEqual(corners.bottomRight, _boundRectCornersForTriangle.bottomRight),
            true
        )
    }

    @Test
    fun originSetCorrectly() {
        val lat = 1.01
        val lon = 3.04
        val boundRect = BoundingRectanglePolygon(emptyList<PointF>(), LatLng(lat, lon))

        assertEquals(boundRect.origin.latitude, lat)
        assertEquals(boundRect.origin.longitude, lon)
    }

    @Test
    fun passEmptyVerticesList() {

        // constructed with empty list of vertices
        val boundRect = BoundingRectanglePolygon(emptyList<PointF>(), LatLng())
        val corners = boundRect.corners

        // should result in all corners being set to default value
        assertEquals(arePointsEqual(corners.topLeft, BoundingRectangleCorners().topLeft), true)
        assertEquals(arePointsEqual(corners.topRight, BoundingRectangleCorners().topRight), true)
        assertEquals(
            arePointsEqual(corners.bottomLeft, BoundingRectangleCorners().bottomLeft),
            true
        )
        assertEquals(
            arePointsEqual(corners.bottomRight, BoundingRectangleCorners().bottomRight),
            true
        )
    }

    @Test
    fun test_getCenterPoint() {
        val boundRect = BoundingRectanglePolygon(_verticesTriangle, LatLng())

        assertEquals(
            arePointsEqual(boundRect.getCenterPoint(), _centerForBoundRectForTriangle),
            true
        )
    }

    @Test
    fun test_getSquareEnlargedByFactor() {

        val boundRect = BoundingRectanglePolygon(_verticesTriangle, LatLng())
        val cornersEnlarged = boundRect.getSquareEnlargedByFactor(2.0f)

        assertEquals(arePointsEqual(cornersEnlarged.topLeft, PointF(-2.0f, -1.5f)), true)
        assertEquals(arePointsEqual(cornersEnlarged.topRight, PointF(2.0f, -1.5f)), true)
        assertEquals(arePointsEqual(cornersEnlarged.bottomLeft, PointF(-2.0f, 2.5f)), true)
        assertEquals(arePointsEqual(cornersEnlarged.bottomRight, PointF(2.0f, 2.5f)), true)

    }
}