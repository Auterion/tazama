package com.auterion.tazama.data

import com.auterion.tazama.presentation.pages.settings.SettingsViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.CancellationException
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class VehicleRepository @Inject constructor(
    private val vehicleService : Vehicle,
    private val settingsViewModel: SettingsViewModel
    ) : CoroutineScope {
    override val coroutineContext: CoroutineContext = Job() + Dispatchers.IO

    private val _vehiclePosition = MutableStateFlow(LatLng(3.0, 46.0))
    val vehiclePosition = _vehiclePosition.asStateFlow()

    init {
        launch {
            settingsViewModel.fakeVehiclePosition.collect { fakeVehiclePosition ->
                if (fakeVehiclePosition) {
                    launchFakeVehicleCollector()
                } else {
                    cancelFakeVehicleCollector()
                }
            }
        }
    }

    fun cancelFakeVehicleCollector() {
        if (isActive) {
            cancel(CancellationException("Use real data"))
        }
    }

    fun launchFakeVehicleCollector() {
        launch {
            vehicleService.vehiclePosition.collect {
                if (settingsViewModel.fakeVehiclePosition.value) {
                    _vehiclePosition.value = it
                } else {
                    _vehiclePosition.value = LatLng(0.0, 0.0)
                }
            }
        }
    }
}
