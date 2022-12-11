package com.auterion.tazama.data.vehicle.service.mavsdk

import com.auterion.tazama.data.vehicle.TelemetryWriter
import com.auterion.tazama.data.vehicle.VehicleWriter
import com.auterion.tazama.data.vehicle.service.VehicleService
import com.google.android.gms.maps.model.LatLng
import io.mavsdk.MavsdkEventQueue
import io.mavsdk.System
import io.mavsdk.mavsdkserver.MavsdkServer
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

class MavsdkService @Inject constructor(
    private val vehicleWriter: VehicleWriter
) : VehicleService {
    private lateinit var drone: System
    private val mavsdkServer = MavsdkServer()
    private val disposables = CopyOnWriteArrayList<Disposable>()

    override fun connect() {
        MavsdkEventQueue.executor().execute {
            drone = System("127.0.0.1", mavsdkServer.run())
            linkTelemetry(drone.telemetry, vehicleWriter.telemetryWriter)
        }
    }

    private fun linkTelemetry(from: io.mavsdk.telemetry.Telemetry, to: TelemetryWriter) {
        linkPosition(from.position, to.positionWriter)
    }

    private fun linkPosition(
        from: Flowable<io.mavsdk.telemetry.Telemetry.Position>,
        to: MutableStateFlow<LatLng>
    ) {
        val positionDisposable = from.subscribe({ position ->
            to.value = LatLng(position.latitudeDeg, position.longitudeDeg)
        }, {})

        disposables.add(positionDisposable)
    }

    override suspend fun destroy() {
        MavsdkEventQueue.executor().execute {
            disposables.forEach { it.dispose() }
            vehicleWriter.reset()
            mavsdkServer.stop()
            mavsdkServer.destroy()
        }
    }
}
