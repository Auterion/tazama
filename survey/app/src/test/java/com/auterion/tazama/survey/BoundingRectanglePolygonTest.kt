package com.auterion.tazama.survey

import com.auterion.tazama.survey.utils.geo.BoundingRectangleCorners
import com.auterion.tazama.survey.utils.geo.BoundingRectanglePolygon
import com.auterion.tazama.survey.utils.geo.PointF
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class BoundingRectanglePolygonTest {
    // triangle
    private val verticesTriangle = listOf(
        PointF(-1.0f, 0.0f), PointF(1.0f, 0.0f), PointF(0.0f, 1.0f)
    )

    private val boundRectCornersForTriangle = BoundingRectangleCorners(
        topLeft = PointF(-1.0f, 0.0f),
        topRight = PointF(1.0f, 0.0f),
        bottomLeft = PointF(-1.0f, 1.0f),
        bottomRight = PointF(1.0f, 1.0f)
    )

    private val centerForBoundRectForTriangle = PointF(0.0f, 0.5f)

    private fun arePointsEqual(a: PointF, b: PointF): Boolean {
        return a.x == b.x && a.y == b.y
    }

    @Test
    fun boundingRectanglePolygon_correctForUnorderedSquare() {
        // coordinate system is aligned with screen, x points from left to right
        // y points from top to bottom

        val vertices = listOf<PointF>(
            PointF(1.0f, -1.0f), // top right
            PointF(-1.0f, 1.0f), // bottom left
            PointF(-1.0f, -1.0f), // top left
            PointF(1.0f, 1.0f) // bottom right
        )

        val boundRect = BoundingRectanglePolygon(vertices)
        val corners = boundRect.corners

        assertTrue(arePointsEqual(corners.topLeft, vertices[2]))
        assertTrue(arePointsEqual(corners.topRight, vertices[0]))
        assertTrue(arePointsEqual(corners.bottomLeft, vertices[1]))
        assertTrue(arePointsEqual(corners.bottomRight, vertices[3]))
    }

    @Test
    fun boundingRectanglePolygon_correctForTriangle() {
        val boundRect = BoundingRectanglePolygon(verticesTriangle)
        val corners = boundRect.corners

        assertTrue(arePointsEqual(corners.topLeft, boundRectCornersForTriangle.topLeft))
        assertTrue(arePointsEqual(corners.topRight, boundRectCornersForTriangle.topRight))
        assertTrue(arePointsEqual(corners.bottomLeft, boundRectCornersForTriangle.bottomLeft))
        assertTrue(arePointsEqual(corners.bottomRight, boundRectCornersForTriangle.bottomRight))
    }

    @Test
    fun boundingRectanglePolygon_passEmptyVerticesListHandled() {
        // constructed with empty list of vertices
        val boundRect = BoundingRectanglePolygon(emptyList())
        val corners = boundRect.corners

        // should result in all corners being set to default value
        assertTrue(arePointsEqual(corners.topLeft, BoundingRectangleCorners().topLeft))
        assertTrue(arePointsEqual(corners.topRight, BoundingRectangleCorners().topRight))
        assertTrue(arePointsEqual(corners.bottomLeft, BoundingRectangleCorners().bottomLeft))
        assertTrue(arePointsEqual(corners.bottomRight, BoundingRectangleCorners().bottomRight))
    }

    @Test
    fun boundingRectanglePolygon_centerIsCorrectForTriangle() {
        val boundRect = BoundingRectanglePolygon(verticesTriangle)

        assertEquals(
            arePointsEqual(boundRect.getCenterPoint(), centerForBoundRectForTriangle),
            true
        )
    }

    @Test
    fun boundingRectanglePolygon_enlargedByFactorCorrectly() {
        val boundRect = BoundingRectanglePolygon(verticesTriangle)
        val cornersEnlarged = boundRect.getSquareEnlargedByFactor(2.0f)

        assertTrue(arePointsEqual(cornersEnlarged.topLeft, PointF(-2.0f, -1.5f)))
        assertTrue(arePointsEqual(cornersEnlarged.topRight, PointF(2.0f, -1.5f)))
        assertTrue(arePointsEqual(cornersEnlarged.bottomLeft, PointF(-2.0f, 2.5f)))
        assertTrue(arePointsEqual(cornersEnlarged.bottomRight, PointF(2.0f, 2.5f)))
    }
}