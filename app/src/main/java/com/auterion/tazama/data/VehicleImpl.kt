package com.auterion.tazama.data

import com.google.android.gms.maps.model.LatLng
import io.mavsdk.MavsdkEventQueue
import io.mavsdk.System
import io.mavsdk.mavsdkserver.MavsdkServer
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class VehicleImpl @Inject constructor() : Vehicle {

    lateinit var drone: System
    var mavSdkServer = MavsdkServer()

    override var vehiclePosition = MutableStateFlow(LatLng(0.0, 0.0))

    init {
        MavsdkEventQueue.executor().execute() {
            drone = System("127.0.0.1", mavSdkServer.run())
            drone.telemetry.position.subscribe({
                vehiclePosition.value = LatLng(it.latitudeDeg, it.longitudeDeg)
            }, {})
        }
    }
}
