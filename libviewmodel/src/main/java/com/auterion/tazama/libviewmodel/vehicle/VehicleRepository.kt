package com.auterion.tazama.libviewmodel.vehicle

import com.auterion.tazama.libvehicle.Vehicle
import com.auterion.tazama.libvehicle.VehicleImpl
import com.auterion.tazama.libvehicle.service.VehicleService
import com.auterion.tazama.libvehicle.service.dummy.DummyService
import com.auterion.tazama.libvehicle.service.mavsdk.MavsdkService
import com.auterion.tazama.libviewmodel.settings.SettingsViewModel.VehicleType
import com.auterion.tazama.libviewmodel.settings.SettingsViewModel.VehicleType.FAKE
import com.auterion.tazama.libviewmodel.settings.SettingsViewModel.VehicleType.MAVSDK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface VehicleRepository {
    val vehicle: Vehicle
}

internal class VehicleRepositoryImpl(vehicleType: Flow<VehicleType>) : VehicleRepository,
    CoroutineScope {
    override val coroutineContext: CoroutineContext = Job() + Dispatchers.IO

    private val vehicleImpl = VehicleImpl()
    override val vehicle: Vehicle = vehicleImpl

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
