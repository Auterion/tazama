package com.auterion.tazama.data.vehicle

import com.auterion.tazama.data.vehicle.VehicleType.FAKE
import com.auterion.tazama.data.vehicle.VehicleType.MAVSDK
import com.auterion.tazama.libvehicle.Vehicle
import com.auterion.tazama.libvehicle.VehicleImpl
import com.auterion.tazama.libvehicle.service.VehicleService
import com.auterion.tazama.libvehicle.service.dummy.DummyService
import com.auterion.tazama.libvehicle.service.mavsdk.MavsdkService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

enum class VehicleType { FAKE, MAVSDK }

class VehicleRepository(vehicleType: Flow<VehicleType>) : CoroutineScope {
    override val coroutineContext: CoroutineContext = Job() + Dispatchers.IO

    private val vehicleImpl = VehicleImpl()
    val vehicle: Vehicle = vehicleImpl

    private var lastVehicleType: VehicleType? = null
    private var vehicleService: VehicleService = DummyService(vehicleImpl)

    init {
        launch {
            vehicleType.collect { vehicleType ->
                if (vehicleType != lastVehicleType) {
                    lastVehicleType = vehicleType
                    swapVehicleType(vehicleType)
                }
            }
        }
    }

    private suspend fun swapVehicleType(vehicleType: VehicleType) {
        vehicleService.destroy()

        vehicleService = when (vehicleType) {
            FAKE -> DummyService(vehicleImpl)
            MAVSDK -> MavsdkService(vehicleImpl)
        }

        vehicleService.connect()
    }
}
