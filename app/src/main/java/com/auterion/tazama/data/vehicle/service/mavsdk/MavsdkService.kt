package com.auterion.tazama.data.vehicle.service.mavsdk

import com.auterion.tazama.data.vehicle.*
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
        linkVelocity(from.velocityNed, to.velocityWriter)
        linkAttitude(from.attitudeEuler, to.attitudeWriter)
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

    private fun linkVelocity(
        from: Flowable<io.mavsdk.telemetry.Telemetry.VelocityNed>,
        to: MutableStateFlow<VelocityNed>
    ) {
        val velocityDisposable = from.subscribe({
            to.value = VelocityNed(
                it.northMS.toDouble(),
                it.eastMS.toDouble(),
                it.downMS.toDouble()
            )
        }, {})

        disposables.add(velocityDisposable)
    }

    private fun linkAttitude(
        from: Flowable<io.mavsdk.telemetry.Telemetry.EulerAngle>,
        to: MutableStateFlow<Euler>
    ) {
        val headingDisposable = from.subscribe({
            to.value = Euler(
                Radian(it.rollDeg.toDouble()),
                Radian(it.pitchDeg.toDouble()),
                Radian(it.yawDeg.toDouble())
            )
        }, {})

        disposables.add(headingDisposable)
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
