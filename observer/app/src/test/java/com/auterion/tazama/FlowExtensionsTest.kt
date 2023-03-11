package com.auterion.tazama

import com.auterion.tazama.libvehicle.Altitude
import com.auterion.tazama.libvehicle.Degrees
import com.auterion.tazama.libvehicle.Distance
import com.auterion.tazama.libvehicle.PositionAbsolute
import com.auterion.tazama.util.distinctUntil
import com.auterion.tazama.util.windowed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FlowExtensionsTest {
    @Test
    fun distinctUntil_emitsFirstElement() = runTest {
        val position = com.auterion.tazama.libvehicle.PositionAbsolute()
        val flow = flowOf(position)

        val collected = flow.distinctUntil(com.auterion.tazama.libvehicle.Distance(0.0)).toList()

        assertEquals(1, collected.size)
        assertEquals(position, collected.first())
    }

    @Test
    fun distinctUntil_emitsElementsIfDistBigEnough() = runTest {
        val position1 = com.auterion.tazama.libvehicle.PositionAbsolute(
            com.auterion.tazama.libvehicle.Degrees(45.97623755731381),
            com.auterion.tazama.libvehicle.Degrees(7.658748816478356),
            com.auterion.tazama.libvehicle.Altitude()
        )
        val position2 = com.auterion.tazama.libvehicle.PositionAbsolute(
            com.auterion.tazama.libvehicle.Degrees(45.97653855167026),
            com.auterion.tazama.libvehicle.Degrees(7.658812707223693),
            com.auterion.tazama.libvehicle.Altitude()
        )
        val position3 = com.auterion.tazama.libvehicle.PositionAbsolute(
            com.auterion.tazama.libvehicle.Degrees(45.97594160439559),
            com.auterion.tazama.libvehicle.Degrees(7.660162518053666),
            com.auterion.tazama.libvehicle.Altitude()
        )
        val flow = flowOf(position1, position2, position3)

        val collected = flow.distinctUntil(com.auterion.tazama.libvehicle.Distance(10.0)).toList()

        assertEquals(3, collected.size)
        assertEquals(listOf(position1, position2, position3), collected)
    }

    @Test
    fun distinctUntil_skipsElementsIfDistTooSmall() = runTest {
        val position1 = com.auterion.tazama.libvehicle.PositionAbsolute(
            com.auterion.tazama.libvehicle.Degrees(45.97623755731381),
            com.auterion.tazama.libvehicle.Degrees(7.658748816478356),
            com.auterion.tazama.libvehicle.Altitude()
        )
        val position2 = com.auterion.tazama.libvehicle.PositionAbsolute(
            com.auterion.tazama.libvehicle.Degrees(45.97653855167026),
            com.auterion.tazama.libvehicle.Degrees(7.658812707223693),
            com.auterion.tazama.libvehicle.Altitude()
        )
        val position3 = com.auterion.tazama.libvehicle.PositionAbsolute(
            com.auterion.tazama.libvehicle.Degrees(45.97594160439559),
            com.auterion.tazama.libvehicle.Degrees(7.660162518053666),
            com.auterion.tazama.libvehicle.Altitude()
        )
        val position4 = com.auterion.tazama.libvehicle.PositionAbsolute(
            com.auterion.tazama.libvehicle.Degrees(46.001554625338855),
            com.auterion.tazama.libvehicle.Degrees(7.730810855763431),
            com.auterion.tazama.libvehicle.Altitude()
        )
        val flow = flowOf(position1, position2, position3, position4)

        val collected = flow.distinctUntil(com.auterion.tazama.libvehicle.Distance(1000.0)).toList()

        assertEquals(2, collected.size)
        assertEquals(listOf(position1, position4), collected)
    }

    @Test
    fun windowed_emitsFirstElement() = runTest {
        val flow = flowOf("a")

        val collected = flow.windowed(1).first()

        assertEquals("a", collected.first())
    }

    @Test
    fun windowed_windowDoesNotExceedSize() = runTest {
        val flow = flowOf("a", "b", "c", "d")

        val collected = flow.windowed(2).toList()

        collected.forEach {
            assert(it.size <= 2)
        }
    }

    @Test
    fun windowed_windowIsCorrect() = runTest {
        val flow = flowOf("a", "b", "c", "d")

        val windowedFlow = flow.windowed(2)

        assertEquals(listOf("a"), windowedFlow.take(1).last())
        assertEquals(listOf("a", "b"), windowedFlow.take(2).last())
        assertEquals(listOf("b", "c"), windowedFlow.take(3).last())
        assertEquals(listOf("c", "d"), windowedFlow.take(4).last())
    }
}