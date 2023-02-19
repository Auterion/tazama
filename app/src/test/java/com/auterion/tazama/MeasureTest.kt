package com.auterion.tazama

import com.auterion.tazama.data.vehicle.Distance
import com.auterion.tazama.data.vehicle.Measure.MeasurementSystem.IMPERIAL
import com.auterion.tazama.data.vehicle.Measure.MeasurementSystem.METRIC
import com.auterion.tazama.data.vehicle.Speed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class MeasureTest {
    @Test
    fun speed_defaultsToZero() {
        assertEquals(0.0, Speed().value, 0.0)
    }

    @Test
    fun speed_isCorrect() {
        assertEquals(24.0, Speed(24.0).value, 0.0)
    }

    @Test
    fun speed_defaultsToMetric() {
        assertEquals(METRIC, Speed().measurementSystem)
    }

    @Test
    fun speed_convertsToImperial() {
        val speedMetric = Speed(42.24)

        val speedImperial = speedMetric.toImperial()

        assertEquals(138.5827, speedImperial.value, 0.0001)
    }

    @Test
    fun speed_constructsAsImperial() {
        assertEquals(IMPERIAL, Speed(measurementSystem = IMPERIAL).measurementSystem)
    }

    @Test
    fun speed_convertsToMetric() {
        val speedImperial = Speed(120.6, IMPERIAL)

        val speedMetric = speedImperial.toMetric()

        assertEquals(36.75888, speedMetric.value, 0.0001)
    }

    @Test
    fun speed_convertsToImperialAndBack() {
        val speedMetric = Speed(12.3)

        val speedImperial = speedMetric.toImperial()
        val speedMetricBack = speedImperial.toMetric()

        assertEquals(speedMetric, speedMetricBack)
    }

    @Test
    fun speed_unitIsCorrectInMetric() {
        assertEquals("m/s", Speed().unit)
    }

    @Test
    fun speed_unitIsCorrectInImperial() {
        assertEquals("ft/s", Speed(measurementSystem = IMPERIAL).unit)
    }

    @Test
    fun speed_equalsOperatorIsCorrect() {
        val speedMetric1 = Speed(23.2)
        val speedMetric2 = Speed(23.2)
        val speedMetric3 = Speed(43.1)
        assertEquals(speedMetric1, speedMetric2)
        assertNotEquals(speedMetric1, speedMetric3)

        val speedImperial1 = Speed(43.1, IMPERIAL)
        val speedImperial2 = Speed(43.1, IMPERIAL)
        val speedImperial3 = Speed(23.2, IMPERIAL)
        assertEquals(speedImperial1, speedImperial2)
        assertNotEquals(speedImperial1, speedImperial3)

        assertNotEquals(speedMetric1, speedImperial1)
        assertNotEquals(speedMetric1, speedImperial3)
    }

    @Test
    fun speed_hashcodeIsCorrect() {
        val speedMetric1 = Speed(23.2)
        val speedMetric2 = Speed(23.2)
        val speedMetric3 = Speed(43.1)
        assertEquals(speedMetric1.hashCode(), speedMetric2.hashCode())
        assertNotEquals(speedMetric1.hashCode(), speedMetric3.hashCode())

        val speedImperial1 = Speed(43.1, IMPERIAL)
        val speedImperial2 = Speed(43.1, IMPERIAL)
        val speedImperial3 = Speed(23.2, IMPERIAL)
        assertEquals(speedImperial1.hashCode(), speedImperial2.hashCode())
        assertNotEquals(speedImperial1.hashCode(), speedImperial3.hashCode())

        assertNotEquals(speedMetric1.hashCode(), speedImperial1.hashCode())
        assertNotEquals(speedMetric1.hashCode(), speedImperial3.hashCode())
    }

    @Test
    fun distance_defaultsToZero() {
        assertEquals(0.0, Distance().value, 0.0)
    }

    @Test
    fun distance_isCorrect() {
        assertEquals(24.0, Distance(24.0).value, 0.0)
    }

    @Test
    fun distance_defaultsToMetric() {
        assertEquals(METRIC, Distance().measurementSystem)
    }

    @Test
    fun distance_convertsToImperial() {
        val distanceMetric = Distance(42.24)

        val distanceImperial = distanceMetric.toImperial()

        assertEquals(138.5827, distanceImperial.value, 0.0001)
    }

    @Test
    fun distance_constructsAsImperial() {
        assertEquals(IMPERIAL, Distance(measurementSystem = IMPERIAL).measurementSystem)
    }

    @Test
    fun distance_convertsToMetric() {
        val distanceImperial = Distance(120.6, IMPERIAL)

        val distanceMetric = distanceImperial.toMetric()

        assertEquals(36.75888, distanceMetric.value, 0.0001)
    }

    @Test
    fun distance_convertsToImperialAndBack() {
        val distanceMetric = Distance(12.3)

        val distanceImperial = distanceMetric.toImperial()
        val distanceMetricBack = distanceImperial.toMetric()

        assertEquals(distanceMetric, distanceMetricBack)
    }

    @Test
    fun distance_unitIsCorrectInMetric() {
        assertEquals("m", Distance().unit)
    }

    @Test
    fun distance_unitIsCorrectInImperial() {
        assertEquals("ft", Distance(measurementSystem = IMPERIAL).unit)
    }

    @Test
    fun distance_equalsOperatorIsCorrect() {
        val distanceMetric1 = Distance(23.2)
        val distanceMetric2 = Distance(23.2)
        val distanceMetric3 = Distance(43.1)
        assertEquals(distanceMetric1, distanceMetric2)
        assertNotEquals(distanceMetric1, distanceMetric3)

        val distanceImperial1 = Distance(43.1, IMPERIAL)
        val distanceImperial2 = Distance(43.1, IMPERIAL)
        val distanceImperial3 = Distance(23.2, IMPERIAL)
        assertEquals(distanceImperial1, distanceImperial2)
        assertNotEquals(distanceImperial1, distanceImperial3)

        assertNotEquals(distanceMetric1, distanceImperial1)
        assertNotEquals(distanceMetric1, distanceImperial3)
    }

    @Test
    fun distance_hashcodeIsCorrect() {
        val distanceMetric1 = Distance(23.2)
        val distanceMetric2 = Distance(23.2)
        val distanceMetric3 = Distance(43.1)
        assertEquals(distanceMetric1.hashCode(), distanceMetric2.hashCode())
        assertNotEquals(distanceMetric1.hashCode(), distanceMetric3.hashCode())

        val distanceImperial1 = Distance(43.1, IMPERIAL)
        val distanceImperial2 = Distance(43.1, IMPERIAL)
        val distanceImperial3 = Distance(23.2, IMPERIAL)
        assertEquals(distanceImperial1.hashCode(), distanceImperial2.hashCode())
        assertNotEquals(distanceImperial1.hashCode(), distanceImperial3.hashCode())

        assertNotEquals(distanceMetric1.hashCode(), distanceImperial1.hashCode())
        assertNotEquals(distanceMetric1.hashCode(), distanceImperial3.hashCode())
    }
}
