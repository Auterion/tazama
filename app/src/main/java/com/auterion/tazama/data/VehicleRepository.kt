package com.auterion.tazama.data

import com.auterion.tazama.presentation.pages.settings.SettingsViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import javax.inject.Inject

class VehicleRepository @Inject constructor(
    private val vehicleService : Vehicle,
    private val settingsViewModel: SettingsViewModel
    ) {
    private var _vehiclePosition = MutableStateFlow<LatLng>(LatLng(3.0,46.0))
    var vehiclePosition = _vehiclePosition.asStateFlow()
    var collectorJob : Job = Job()

    init {
        CoroutineScope(Job() + Dispatchers.Main).launch {
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
        if (collectorJob.isActive) {
            collectorJob.cancel(CancellationException("Use real data"))
        }
    }

    fun launchFakeVehicleCollector() {
        collectorJob = CoroutineScope(Job() + Dispatchers.Main).launch {
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
