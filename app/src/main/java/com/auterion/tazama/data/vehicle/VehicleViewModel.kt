package com.auterion.tazama.data.vehicle

import androidx.lifecycle.ViewModel
import com.auterion.tazama.util.FlowHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class VehicleViewModel @Inject constructor(
    vehicleRepository: VehicleRepository,
    measureSystem: FlowHolder
) : ViewModel() {
    val vehiclePosition = vehicleRepository.vehicle.telemetry.position
        .combine(measureSystem.flow) { pos, measureSystem -> pos?.toSystem(measureSystem) }

    val vehicleVelocity = vehicleRepository.vehicle.telemetry.velocity
        .combine(measureSystem.flow) { velocity, measureSystem -> velocity?.toSystem(measureSystem) }

    val homePosition = vehicleRepository.vehicle.telemetry.homePosition
        .combine(measureSystem.flow) { pos, measureSystem -> pos?.toSystem(measureSystem) }

    val distanceToHome = vehicleRepository.vehicle.telemetry.distanceToHome
        .combine(measureSystem.flow) { dist, measureSystem -> dist?.toSystem(measureSystem) }

    val groundSpeed = vehicleRepository.vehicle.telemetry.groundSpeed
        .combine(measureSystem.flow) { speed, measureSystem -> speed?.toSystem(measureSystem) }

    val vehicleAttitude = vehicleRepository.vehicle.telemetry.attitude
    val videoStreamInfo = vehicleRepository.vehicle.camera.videoStreamInfo
}
