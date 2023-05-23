package com.auterion.tazama.libviewmodel.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import com.auterion.tazama.libvehicle.Measure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class SettingsViewModel(
    private val application: Application,
    private val preferences: Preferences
) : ViewModel() {
    private fun getContext(): Context {
        return application.applicationContext
    }

    // Vehicle
    enum class VehicleType { FAKE, MAVSDK }

    private val _vehicleType =
        MutableStateFlow(preferences.getVehicleType().toSettings())
    val vehicleType = _vehicleType.asStateFlow()

    fun setVehicleType(vehicleType: VehicleType) {
        preferences.setVehicleType(vehicleType.toPrefs())
        _vehicleType.value = vehicleType
    }

    // Map
    enum class MapType { SATELLITE, NORMAL, HYBRID }

    private var _currentMapType =
        MutableStateFlow(preferences.getMapType().toSettings())
    val currentMapType = _currentMapType.asStateFlow()

    fun setSatelliteMap(mapType: MapType) {
        preferences.setMapType(mapType.toPrefs())
        _currentMapType.value = mapType
    }

    // Measure System
    enum class MeasureSystem {
        METRIC, IMPERIAL;

        internal fun toMeasurement(): Measure.MeasurementSystem {
            return when (this) {
                METRIC -> Measure.MeasurementSystem.METRIC
                IMPERIAL -> Measure.MeasurementSystem.IMPERIAL
            }
        }
    }

    private val _measureSystem =
        MutableStateFlow(preferences.getMeasureSystem().toSettings())
    val measureSystem = _measureSystem.asStateFlow()

    fun setMeasureSystem(measureSystem: MeasureSystem) {
        println(measureSystem.toString())
        preferences.setMeasureSystem(measureSystem.toPrefs())
        _measureSystem.value = measureSystem
    }

    private fun Preferences.VehicleType.toSettings(): VehicleType {
        return when (this) {
            Preferences.VehicleType.FAKE -> VehicleType.FAKE
            Preferences.VehicleType.MAVSDK -> VehicleType.MAVSDK
        }
    }

    private fun VehicleType.toPrefs(): Preferences.VehicleType {
        return when (this) {
            VehicleType.FAKE -> Preferences.VehicleType.FAKE
            VehicleType.MAVSDK -> Preferences.VehicleType.MAVSDK
        }
    }

    private fun Preferences.MapType.toSettings(): MapType {
        return when (this) {
            Preferences.MapType.SATELLITE -> MapType.SATELLITE
            Preferences.MapType.NORMAL -> MapType.NORMAL
            Preferences.MapType.HYBRID -> MapType.HYBRID
        }
    }

    private fun MapType.toPrefs(): Preferences.MapType {
        return when (this) {
            MapType.SATELLITE -> Preferences.MapType.SATELLITE
            MapType.NORMAL -> Preferences.MapType.NORMAL
            MapType.HYBRID -> Preferences.MapType.HYBRID
        }
    }

    private fun Preferences.MeasureSystem.toSettings(): MeasureSystem {
        return when (this) {
            Preferences.MeasureSystem.METRIC -> MeasureSystem.METRIC
            Preferences.MeasureSystem.IMPERIAL -> MeasureSystem.IMPERIAL
        }
    }

    private fun MeasureSystem.toPrefs(): Preferences.MeasureSystem {
        return when (this) {
            MeasureSystem.METRIC -> Preferences.MeasureSystem.METRIC
            MeasureSystem.IMPERIAL -> Preferences.MeasureSystem.IMPERIAL
        }
    }
}
