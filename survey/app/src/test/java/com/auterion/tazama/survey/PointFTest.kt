package com.auterion.tazama.survey

import com.auterion.tazama.survey.utils.geo.PointF
import junit.framework.TestCase.assertEquals
import org.junit.Test

class PointFTest {
    @Test
    fun point_operatorMinus() {

        val point1 = PointF(0.0f, 0.0f)
        val point2 = PointF(1.0f, -1.0f)

        val res = point1 - point2

        assertEquals(res.x, -1.0f)
        assertEquals(res.y, 1.0f)

    }

    @Test
    fun point_multiplyByFactor() {
        var point = PointF(0.0f, 0.0f)
        var res = point * 1.0f

        assertEquals(res.x, 0.0f)
        assertEquals(res.y, 0.0f)

        point = PointF(1.0f, -1.0f)
        point = point * 2.0f

        assertEquals(point.x, 2.0f)
        assertEquals(point.y, 2.0f)
    }
}