package com.auterion.tazama.data.vehicle

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class VehicleViewModel @Inject constructor(
    vehicleRepository: VehicleRepository,
    measurementSystemFlow: Flow<Measure.MeasurementSystem>
) : ViewModel() {
    val vehiclePosition = vehicleRepository.vehicle.telemetry.position
        .combine(measurementSystemFlow) { position, measureSystem ->
            when (measureSystem) {
                Measure.MeasurementSystem.METRIC -> position.toMetric()
                Measure.MeasurementSystem.IMPERIAL -> position.toImperial()
            }
        }
    val vehicleVelocity = vehicleRepository.vehicle.telemetry.velocity
    val vehicleAttitude = vehicleRepository.vehicle.telemetry.attitude
    val videoStreamInfo = vehicleRepository.vehicle.camera.videoStreamInfo
    val homePosition = vehicleRepository.vehicle.telemetry.homePosition
}
