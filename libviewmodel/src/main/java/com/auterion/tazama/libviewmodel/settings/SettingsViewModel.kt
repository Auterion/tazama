package com.auterion.tazama.libviewmodel.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(private val application: Application) : ViewModel() {
    private fun getContext(): Context {
        return application.applicationContext
    }

    // Vehicle
    enum class VehicleType { FAKE, MAVSDK }

    private val _vehicleType =
        MutableStateFlow(Preferences.getVehicleType(getContext()).toSettings())
    val vehicleType = _vehicleType.asStateFlow()

    fun setVehicleType(vehicleType: VehicleType) {
        Preferences.setVehicleType(getContext(), vehicleType.toPrefs())
        _vehicleType.value = vehicleType
    }

    // Map
    enum class MapType { SATELLITE, NORMAL, HYBRID }

    private var _currentMapType =
        MutableStateFlow(Preferences.getMapType(getContext()).toSettings())
    val currentMapType = _currentMapType.asStateFlow()

    fun setSatelliteMap(mapType: MapType) {
        Preferences.setMapType(getContext(), mapType.toPrefs())
        _currentMapType.value = mapType
    }

    // Measure System
    enum class MeasureSystem { METRIC, IMPERIAL }

    private val _measureSystem =
        MutableStateFlow(Preferences.getMeasureSystem(getContext()).toSettings())
    val measureSystem = _measureSystem.asStateFlow()

    fun setMeasureSystem(measureSystem: MeasureSystem) {
        println(measureSystem.toString())
        Preferences.setMeasureSystem(getContext(), measureSystem.toPrefs())
        _measureSystem.value = measureSystem
    }

    private fun Preferences.VehicleType.toSettings(): VehicleType {
        return when (this) {
            Preferences.VehicleType.FAKE -> com.auterion.tazama.libviewmodel.settings.SettingsViewModel.VehicleType.FAKE
            Preferences.VehicleType.MAVSDK -> com.auterion.tazama.libviewmodel.settings.SettingsViewModel.VehicleType.MAVSDK
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
            Preferences.MapType.SATELLITE -> com.auterion.tazama.libviewmodel.settings.SettingsViewModel.MapType.SATELLITE
            Preferences.MapType.NORMAL -> com.auterion.tazama.libviewmodel.settings.SettingsViewModel.MapType.NORMAL
            Preferences.MapType.HYBRID -> com.auterion.tazama.libviewmodel.settings.SettingsViewModel.MapType.HYBRID
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
            Preferences.MeasureSystem.METRIC -> com.auterion.tazama.libviewmodel.settings.SettingsViewModel.MeasureSystem.METRIC
            Preferences.MeasureSystem.IMPERIAL -> com.auterion.tazama.libviewmodel.settings.SettingsViewModel.MeasureSystem.IMPERIAL
        }
    }

    private fun MeasureSystem.toPrefs(): Preferences.MeasureSystem {
        return when (this) {
            MeasureSystem.METRIC -> Preferences.MeasureSystem.METRIC
            MeasureSystem.IMPERIAL -> Preferences.MeasureSystem.IMPERIAL
        }
    }
}