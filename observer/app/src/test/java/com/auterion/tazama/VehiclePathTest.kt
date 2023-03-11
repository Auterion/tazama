package com.auterion.tazama

import com.auterion.tazama.data.vehicle.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VehiclePathTest {
    @Test
    fun vehiclePath_initToEmptyList() = runTest {
        val position = MutableSharedFlow<PositionAbsolute>()
        val vehiclePath = VehiclePath(position)

        assertEquals(0, vehiclePath.path.value.size)
    }

    @Test
    fun vehiclePath_emitsOneElement() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val position = MutableSharedFlow<PositionAbsolute>()
        val vehiclePath = VehiclePath(position, ioDispatcher = testDispatcher)
        launch { position.emit(PositionAbsolute()) }
        advanceUntilIdle()

        val firstElem = vehiclePath.path.value

        val expectedFirstElem = listOf(LatLng(0.0, 0.0))
        assertEquals(expectedFirstElem, firstElem)
    }

    @Test
    fun vehiclePath_honorsMinDistance() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val position = MutableSharedFlow<PositionAbsolute>()
        val position1 = PositionAbsolute(
            Degrees(45.97623755731381),
            Degrees(7.658748816478356),
            Altitude()
        )
        val position2 = PositionAbsolute(
            Degrees(45.97653855167026),
            Degrees(7.658812707223693),
            Altitude()
        )
        val position3 = PositionAbsolute(
            Degrees(45.97594160439559),
            Degrees(7.660162518053666),
            Altitude()
        )
        val position4 = PositionAbsolute(
            Degrees(46.001554625338855),
            Degrees(7.730810855763431),
            Altitude()
        )
        val vehiclePath = VehiclePath(position, Distance(1000.0), ioDispatcher = testDispatcher)
        launch {
            position.emit(position1)
            position.emit(position2)
            position.emit(position3)
            position.emit(position4)
        }
        advanceUntilIdle()

        val emittedPath = vehiclePath.path.value

        val expectedPath = listOf(
            LatLng(position1.lat.value, position1.lon.value),
            LatLng(position4.lat.value, position4.lon.value)
        )
        assertEquals(expectedPath, emittedPath)
    }

    @Test
    fun vehiclePath_honorsMaxPathLength() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val position = MutableSharedFlow<PositionAbsolute>()
        val position1 = PositionAbsolute(
            Degrees(45.97623755731381),
            Degrees(7.658748816478356),
            Altitude()
        )
        val position2 = PositionAbsolute(
            Degrees(45.97653855167026),
            Degrees(7.658812707223693),
            Altitude()
        )
        val position3 = PositionAbsolute(
            Degrees(45.97594160439559),
            Degrees(7.660162518053666),
            Altitude()
        )
        val position4 = PositionAbsolute(
            Degrees(46.001554625338855),
            Degrees(7.730810855763431),
            Altitude()
        )
        val vehiclePath = VehiclePath(position, Distance(1.0), 2, testDispatcher)
        launch {
            position.emit(position1)
            position.emit(position2)
            position.emit(position3)
            position.emit(position4)
        }
        advanceUntilIdle()

        val emittedPath = vehiclePath.path.value

        val expectedPath = listOf(
            LatLng(position3.lat.value, position3.lon.value),
            LatLng(position4.lat.value, position4.lon.value)
        )
        assertEquals(expectedPath, emittedPath)
    }

    @Test
    fun vehiclePath_clearWorks() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val position = MutableSharedFlow<PositionAbsolute>()
        val position1 = PositionAbsolute(
            Degrees(45.97623755731381),
            Degrees(7.658748816478356),
            Altitude()
        )
        val position2 = PositionAbsolute(
            Degrees(45.97653855167026),
            Degrees(7.658812707223693),
            Altitude()
        )
        val position3 = PositionAbsolute(
            Degrees(45.97594160439559),
            Degrees(7.660162518053666),
            Altitude()
        )
        val position4 = PositionAbsolute(
            Degrees(46.001554625338855),
            Degrees(7.730810855763431),
            Altitude()
        )
        val vehiclePath = VehiclePath(position, Distance(1.0), 10, testDispatcher)
        launch {
            position.emit(position1)
            position.emit(position2)
            position.emit(position3)
            position.emit(position4)
        }

        advanceUntilIdle()
        assertEquals(4, vehiclePath.path.value.size)

        vehiclePath.clear()
        advanceUntilIdle()
        assertEquals(0, vehiclePath.path.value.size)
    }
}
