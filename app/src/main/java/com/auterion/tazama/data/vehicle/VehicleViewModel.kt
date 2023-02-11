package com.auterion.tazama.data.vehicle

import androidx.lifecycle.ViewModel
import com.auterion.tazama.util.FlowHolder
import com.auterion.tazama.util.GeoUtils
import com.auterion.tazama.util.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

@HiltViewModel
class VehicleViewModel @Inject constructor(
    vehicleRepository: VehicleRepository,
    measurementSystemFlow: FlowHolder
) : ViewModel() {
    val vehiclePosition = vehicleRepository.vehicle.telemetry.position
        .combine(measurementSystemFlow.flow) { position, measureSystem ->
            when (measureSystem) {
                Preferences.MeasureSystem.METRIC -> position.toMetric()
                Preferences.MeasureSystem.IMPERIAL -> position.toImperial()
            }
        }
    val vehicleVelocity = vehicleRepository.vehicle.telemetry.velocity
    val vehicleAttitude = vehicleRepository.vehicle.telemetry.attitude
    val videoStreamInfo = vehicleRepository.vehicle.camera.videoStreamInfo
    val homePosition = vehicleRepository.vehicle.telemetry.homePosition

    val distanceToHome: Flow<Distance> = vehicleRepository.vehicle.telemetry.homePosition
        .combine(measurementSystemFlow.flow) { home, measurementSystem ->

            var dist = Distance(0.0)
            if (home.isValid()) {
                dist = GeoUtils.distanceBetween(
                    Degrees(homePosition.value.lat!!.value),
                    Degrees(homePosition.value.lon!!.value),
                    Degrees(vehiclePosition.first().lat.value),
                    Degrees(vehiclePosition.first().lon.value)
                )
            }
            when (measurementSystem) {
                Preferences.MeasureSystem.METRIC -> dist
                Preferences.MeasureSystem.IMPERIAL -> dist.toImperial()
            }
        }


    val distAboveHome: Flow<Altitude> = vehicleRepository.vehicle.telemetry.homePosition
        .combine(measurementSystemFlow.flow) { _, measurementSystem ->
            var distAboveHome = Altitude(0.0)

            if (homePosition.value.isValid()) {
                distAboveHome = vehiclePosition.first().alt - homePosition.value.alt!!
            }

            when (measurementSystem) {
                Preferences.MeasureSystem.METRIC -> distAboveHome
                Preferences.MeasureSystem.IMPERIAL -> distAboveHome.toImperial()
            }
        }
    val groundSpeed: Flow<Speed> = vehicleRepository.vehicle.telemetry.velocity
        .combine(measurementSystemFlow.flow) { velocity, measurementSystem ->

            var speed = Speed(
                sqrt(velocity.vx.value.pow(2) + velocity.vy.value.pow(2))
            )

            when (measurementSystem) {
                Preferences.MeasureSystem.METRIC -> speed
                Preferences.MeasureSystem.IMPERIAL -> speed.toImperial()
            }
        }
}
