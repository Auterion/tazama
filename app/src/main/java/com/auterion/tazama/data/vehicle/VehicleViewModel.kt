package com.auterion.tazama.data.vehicle

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VehicleViewModel @Inject constructor(vehicleRepository: VehicleRepository) : ViewModel() {
    val vehiclePosition = vehicleRepository.vehicle.telemetry.position
}
