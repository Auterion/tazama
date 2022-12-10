package com.auterion.tazama.data

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VehicleViewModel @Inject constructor(vehicleRepository: VehicleRepository) : ViewModel() {
    private val _vehiclePosition = vehicleRepository.vehiclePosition
    val vehiclePosition = _vehiclePosition
}
