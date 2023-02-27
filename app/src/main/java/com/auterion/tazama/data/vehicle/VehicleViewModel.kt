package com.auterion.tazama.data.vehicle

import androidx.lifecycle.ViewModel
import com.auterion.tazama.presentation.pages.main.TelemetryDisplayNumber
import com.auterion.tazama.util.FlowHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class VehicleViewModel @Inject constructor(
    vehicleRepository: VehicleRepository,
    measureSystem: FlowHolder
) : ViewModel() {
    val vehiclePosition = vehicleRepository.vehicle.telemetry.position
        .combine(measureSystem.flow) { pos, measureSystem -> pos?.toSystem(measureSystem) }

    val horizontalDistanceToHome = vehicleRepository.vehicle.telemetry.distanceToHome
        .combine(measureSystem.flow) { dist, measureSystem ->
            when (dist) {
                null -> TelemetryDisplayNumber(unit = Distance(measurementSystem = measureSystem).unit)
                else -> {
                    val distMapped = dist.horizontal.toSystem(measureSystem)
                    TelemetryDisplayNumber(
                        distMapped.value,
                        distMapped.unit
                    )
                }
            }
        }

    val vehiclePath = VehiclePath(vehicleRepository.vehicle.telemetry.position)

    val heightAboveHome = vehicleRepository.vehicle.telemetry.distanceToHome
        .combine(measureSystem.flow) { dist, measureSystem ->
            when (dist) {
                null -> TelemetryDisplayNumber(unit = Distance(measurementSystem = measureSystem).unit)
                else -> {
                    val distMapped = dist.toSystem(measureSystem).vertical
                    TelemetryDisplayNumber(
                        distMapped.value, distMapped.unit
                    )
                }
            }
        }

    val groundSpeed = vehicleRepository.vehicle.telemetry.groundSpeed
        .combine(measureSystem.flow) { speed, measureSystem ->
            when (speed) {
                null -> TelemetryDisplayNumber(unit = Speed(measurementSystem = measureSystem).unit)
                else -> {
                    val speedMapped = speed.toSystem(measureSystem)
                    TelemetryDisplayNumber(speedMapped.value, speedMapped.unit)
                }
            }
        }

    val vehicleAttitude = vehicleRepository.vehicle.telemetry.attitude
    val vehicleHeading = vehicleRepository.vehicle.telemetry.attitude.map { attitude ->
        when (attitude) {
            null -> TelemetryDisplayNumber(unit = "deg")
            else -> TelemetryDisplayNumber(attitude.yaw.toDegrees().value, unit = "deg")
        }
    }
    val videoStreamInfo = vehicleRepository.vehicle.camera.videoStreamInfo

    fun resetFlightPath() {
    }
}
