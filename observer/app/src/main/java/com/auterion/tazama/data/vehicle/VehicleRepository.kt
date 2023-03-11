package com.auterion.tazama.data.vehicle

import com.auterion.tazama.libvehicle.Vehicle
import com.auterion.tazama.libvehicle.VehicleImpl
import com.auterion.tazama.libvehicle.service.VehicleService
import com.auterion.tazama.libvehicle.service.dummy.DummyService
import com.auterion.tazama.libvehicle.service.mavsdk.MavsdkService
import com.auterion.tazama.presentation.pages.settings.SettingsViewModel
import com.auterion.tazama.presentation.pages.settings.SettingsViewModel.VehicleType
import com.auterion.tazama.presentation.pages.settings.SettingsViewModel.VehicleType.FAKE
import com.auterion.tazama.presentation.pages.settings.SettingsViewModel.VehicleType.MAVSDK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class VehicleRepository @Inject constructor(
    private val settingsViewModel: SettingsViewModel
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = Job() + Dispatchers.IO

    private val vehicleImpl = VehicleImpl()
    val vehicle: Vehicle = vehicleImpl

    private var lastVehicleType: VehicleType? = null
    private var vehicleService: VehicleService = DummyService(vehicleImpl)

    init {
        launch {
            settingsViewModel.vehicleType.collect { vehicleType ->
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
