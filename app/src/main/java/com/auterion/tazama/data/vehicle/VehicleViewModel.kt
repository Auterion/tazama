package com.auterion.tazama.data.vehicle

import androidx.lifecycle.ViewModel
import com.auterion.tazama.util.FlowHolder
import com.auterion.tazama.util.GeoUtils
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val pathPointsMinDistance = 1.0   // meters
private const val pathPointsMax = 5000

@HiltViewModel
class VehicleViewModel @Inject constructor(
    vehicleRepository: VehicleRepository,
    measureSystem: FlowHolder
) : ViewModel() {
    val vehiclePosition = vehicleRepository.vehicle.telemetry.position
        .combine(measureSystem.flow) { pos, measureSystem -> pos?.toSystem(measureSystem) }

    val horizonalDistanceToHome = vehicleRepository.vehicle.telemetry.distanceToHome
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
    private val vehiclePathPoints: MutableList<LatLng> = mutableListOf()

    val vehiclePath = vehicleRepository.vehicle.telemetry.position
        .map { position ->

            position?.let {
                if (vehiclePathPoints.size > 0) {
                    val dist = GeoUtils.distanceBetween(
                        position.lat,
                        position.lon,
                        Degrees(vehiclePathPoints.last().latitude),
                        Degrees(vehiclePathPoints.last().longitude)
                    )

                    when {
                        dist.value > pathPointsMinDistance -> {
                            vehiclePathPoints.add(LatLng(position.lat.value, position.lon.value))
                        }
                        else -> Unit
                    }
                } else {
                    vehiclePathPoints.add(LatLng(position.lat.value, position.lon.value))
                }
            }

            if (vehiclePathPoints.size > pathPointsMax) {
                vehiclePathPoints.removeAt(0)
            }

            vehiclePathPoints.toList()
        }

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
}

data class TelemetryDisplayNumber(val value: Double? = null, val unit: String = "") {
}
