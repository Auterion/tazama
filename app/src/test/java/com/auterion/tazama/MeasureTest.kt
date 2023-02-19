package com.auterion.tazama

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
}
