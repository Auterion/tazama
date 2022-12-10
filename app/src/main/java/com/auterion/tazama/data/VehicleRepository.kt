package com.auterion.tazama.data

import com.auterion.tazama.presentation.pages.settings.SettingsViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class VehicleRepository @Inject constructor(
    private val vehicleService : VehicleImpl,
    private val vehicleDummyService : VehicleDummyImpl,
    private val settingsViewModel: SettingsViewModel
    ) : CoroutineScope {

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.IO
    private var collectorJob : Job? = null

    private val _vehiclePosition = MutableStateFlow(LatLng(0.0, 0.0))
    val vehiclePosition = _vehiclePosition.asStateFlow()

    init {
        launch {
            settingsViewModel.fakeVehiclePosition.collect { fakeVehiclePosition ->

                collectorJob?.cancelAndJoin()

               if (fakeVehiclePosition) {
                    collectorJob = launch {
                        vehicleDummyService.vehiclePosition.collect() {
                            coroutineContext.ensureActive()
                            _vehiclePosition.value = it
                        }
                    }
               } else {
                    collectorJob = launch {
                        vehicleService.vehiclePosition.collect() {
                            coroutineContext.ensureActive()
                            _vehiclePosition.value = it
                        }
                    }
               }
            }
        }
    }
}
