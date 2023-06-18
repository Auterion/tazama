package com.auterion.tazama.survey

import com.auterion.tazama.survey.utils.geo.Line
import com.auterion.tazama.survey.utils.geo.PointF
import com.auterion.tazama.survey.utils.geo.rotateAroundCenter
import junit.framework.TestCase.assertEquals
import org.junit.Test
import kotlin.math.PI

class LineTest {
    @Test
    fun starEndAreSetCorrectly() {
        val start = PointF(100.0f, -0.035f)
        val end = PointF(-1.57f, 1010.58f)
        val line = Line(start, end)

        assertEquals(line.start.x, start.x)
        assertEquals(line.start.y, start.y)
        assertEquals(line.end.x, end.x)
        assertEquals(line.end.y, end.y)

    }

    @Test
    fun lineParamsAreCorrect() {
        val start = PointF(0.0f, 0.0f)
        val end = PointF(1.0f, 1.0f)

        val line = Line(start, end)
        assertEquals(line.m, 1.0f)
        assertEquals(line.b, 0.0f)

        val verticalLineUp = Line(start, PointF(start.x, start.y + 1.0f))
        assertEquals(verticalLineUp.m, Float.POSITIVE_INFINITY)
        val verticalLineDown = Line(start, PointF(start.x, start.y - 1.0f))
        assertEquals(verticalLineDown.m, Float.NEGATIVE_INFINITY)
    }

    @Test
    fun pointIsOnLine() {
        val point = PointF(1.0f, 1.0f)
        val start = PointF(0.0f, 0.0f)
        val end = PointF(1.0f, 1.0f)

        val line = Line(start, end)

        assertEquals(line.pointIsOnLine(point), true)

        // chose random x and plug it into line equation, point should be on line
        var randomX = 0.57f
        assertEquals(line.pointIsOnLine(PointF(randomX, line.m * randomX + line.b)), true)

        // test some long lines
        val longStart = PointF(-10000.577777778f, 300.556f)
        val longEnd = PointF(25467111777777.1f, -27487312.578f)
        val longLine = Line(longStart, longEnd)

        randomX = -557.777f
        assertEquals(
            longLine.pointIsOnLine(PointF(randomX, -longLine.m * randomX + longLine.b)),
            true
        )
    }

    @Test
    fun intersectLongLines() {
        // this test has been very useful to identify numerical problem that arise when rotating lines
        // and finding intersections. At first a fixed delta was used for comparison but that
        // turned out not to work due to seemingly loss of precision when doing the rotations.
        // Currently we are using a delta as a function of the line length which seems to work better.
        // Could also try to use Double for everything or Kahan summation.

        val line1 = Line(PointF(-100000.0f, 25000.0f), PointF(100000.0f, 25000.0f))
        val line2 = Line(PointF(-1.0f, -1000.0f), PointF(-1.0f, 26000.0f))

        val intersectPoint = PointF(-1.0f, 25000.0f)

        assertEquals(line1.intersect(line2)?.point?.x, intersectPoint.x)
        assertEquals(line1.intersect(line2)?.point?.y, intersectPoint.y)


        // rotate the two lines and the intersection point using various angles
        // the new intersection point should equal the rotates intersection point
        var angle = 0.0
        for (i in 0..100) {
            val intersectRot = intersectPoint.rotateAroundCenter(PointF(), angle)
            val line1Rot = line1.rotateAroundCenter(PointF(0.0f, 0.0f), angle)
            val line2Rot = line2.rotateAroundCenter(PointF(0.0f, 0.0f), angle)

            assertEquals(line1Rot.intersect(line2Rot)?.point?.x!!, intersectRot.x, 0.5f)
            assertEquals(line1Rot.intersect(line2Rot)?.point?.y!!, intersectRot.y, 0.5f)

            angle += 2 * PI * 0.01
        }
    }

    @Test
    fun lineRotationCorrect() {
        val line = Line(PointF(-1.0f, 1.0f), PointF(1 - 0f, 1 - 0f))

        val lineRot = line.rotateAroundCenter(PointF(), -PI / 2)

        assertEquals(lineRot.start.x, 1.0f)
        assertEquals(lineRot.end.x, 1.0f)
        assertEquals(lineRot.start.y, 1.0f)
        assertEquals(lineRot.end.y, -1.0f)

    }
}