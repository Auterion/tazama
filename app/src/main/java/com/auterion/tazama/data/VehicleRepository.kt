package com.example.tazama.data

import com.auterion.tazama.data.VehicleInterface
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class VehicleRepository @Inject constructor(private val vehicleService : VehicleInterface) {
    private val _vehiclePosition = vehicleService.vehiclePosition
    val vehiclePosition = _vehiclePosition.asStateFlow()
}
