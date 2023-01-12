package com.auterion.tazama.data.vehicle.service.dummy

import com.auterion.tazama.data.vehicle.Euler
import com.auterion.tazama.data.vehicle.Radian
import com.auterion.tazama.data.vehicle.VehicleWriter
import com.auterion.tazama.data.vehicle.VelocityNed
import com.auterion.tazama.data.vehicle.service.VehicleService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class DummyService @Inject constructor(
    private val vehicleWriter: VehicleWriter
) : VehicleService, CoroutineScope {
    override val coroutineContext: CoroutineContext = Job() + Dispatchers.IO

    private val emitJobs = CopyOnWriteArrayList<Job>()

    override fun connect() {
        emitJobs.add(launch { emitPosition() })
        emitJobs.add(launch { emitVelocity() })
        emitJobs.add(launch { emitAttitude() })
    }

    private tailrec suspend fun emitPosition() {
        if (!isActive) {
            return
        }

        println("Emitting dummy position")
        vehicleWriter.telemetryWriter.positionWriter.value =
            LatLng(Random.nextDouble(3.0, 4.0), Random.nextDouble(46.0, 47.0))

        delay(1000)
        emitPosition()
    }

    private tailrec suspend fun emitVelocity() {
        if (!isActive) {
            return
        }

        println("Emitting dummy velocity")
        vehicleWriter.telemetryWriter.velocityWriter.value =
            VelocityNed(0.0, 0.0, 0.0)

        delay(1000)
        emitVelocity()
    }


    private tailrec suspend fun emitAttitude() {
        if (!isActive) {
            return
        }

        println("Emitting dummy attitude")
        vehicleWriter.telemetryWriter.attitudeWriter.value =
            Euler(Radian(0.0), Radian(0.0), Radian(0.0))

        delay(1000)
        emitAttitude()
    }

    override suspend fun destroy() {
        emitJobs.forEach { it.cancelAndJoin() }
        vehicleWriter.reset()
    }
}
