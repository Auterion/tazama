package com.auterion.tazama.data.vehicle

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VehicleViewModel @Inject constructor(vehicleRepository: VehicleRepository) : ViewModel() {
    val vehiclePosition = vehicleRepository.vehicle.telemetry.position
    val vehicleVelocity = vehicleRepository.vehicle.telemetry.velocity
    val vehicleAttitude = vehicleRepository.vehicle.telemetry.attitude
    val videoStreamInfo = vehicleRepository.vehicle.camera.videoStreamInfo
    val homePosition = vehicleRepository.vehicle.telemetry.homePosition
}
