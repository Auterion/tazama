package com.auterion.tazama.data

import com.google.android.gms.maps.model.LatLng
import io.mavsdk.MavsdkEventQueue
import io.mavsdk.System
import io.mavsdk.mavsdkserver.MavsdkServer
import io.mavsdk.telemetry.Telemetry
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class VehicleImpl @Inject constructor() : Vehicle {

    lateinit var drone : io.mavsdk.System
    var mavSdkServer = MavsdkServer()

    override var vehiclePosition = MutableStateFlow<LatLng>(LatLng(0.0,0.0))

    init {
        MavsdkEventQueue.executor().execute() {
            drone = System("127.0.0.1", mavSdkServer.run())
            drone.telemetry.position.subscribe({
                vehiclePosition.value = LatLng(it.latitudeDeg, it.longitudeDeg)
            }, {})
        }
    }
}