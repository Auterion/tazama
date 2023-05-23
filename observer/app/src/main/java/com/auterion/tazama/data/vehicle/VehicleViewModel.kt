package com.auterion.tazama.data.vehicle

import androidx.lifecycle.ViewModel
import com.auterion.tazama.libui.presentation.pages.main.TelemetryDisplayNumber
import com.auterion.tazama.libvehicle.Distance
import com.auterion.tazama.libvehicle.Measure
import com.auterion.tazama.libvehicle.Speed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class VehicleViewModel(
    vehicleRepository: VehicleRepository,
    measureSystem: Flow<Measure.MeasurementSystem>
) : ViewModel() {
    val vehiclePosition = vehicleRepository.vehicle.telemetry.position
        .combine(measureSystem) { pos, measureSystem -> pos?.toSystem(measureSystem) }

    val horizontalDistanceToHome = vehicleRepository.vehicle.telemetry.distanceToHome
        .combine(measureSystem) { dist, measureSystem ->
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
        .combine(measureSystem) { dist, measureSystem ->
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
        .combine(measureSystem) { speed, measureSystem ->
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
        vehiclePath.clear()
    }
}
