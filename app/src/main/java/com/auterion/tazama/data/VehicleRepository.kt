package com.auterion.tazama.data

import com.auterion.tazama.presentation.pages.settings.SettingsViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import java.util.concurrent.CancellationException
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class VehicleRepository @Inject constructor(
    private val vehicleService : VehicleImpl,
    private val vehicleDummyService : VehicleDummyImpl,
    private val settingsViewModel: SettingsViewModel
    ) : CoroutineScope {

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.IO

    var vehicle : Vehicle = vehicleService

    //private var _vehiclePosition = vehicleService.vehiclePosition
    //var vehiclePosition = _vehiclePosition.asStateFlow()

    init {
        launch {
            settingsViewModel.fakeVehiclePosition.collect { fakeVehiclePosition ->
               if (fakeVehiclePosition) {
                   vehicle = vehicleDummyService
               } else {
                   vehicle = vehicleService
               }
            }
        }
    }
}
