package com.auterion.tazama.data.vehicle.service.dummy

import com.auterion.tazama.data.vehicle.VehicleWriter
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

    override suspend fun destroy() {
        emitJobs.forEach { it.cancelAndJoin() }
        vehicleWriter.reset()
    }
}
