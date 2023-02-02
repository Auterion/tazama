package com.auterion.tazama.presentation.pages.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import com.auterion.tazama.util.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    private val context: Context

    // Vehicle
    enum class VehicleType { FAKE, MAVSDK }

    private val _vehicleType = MutableStateFlow(Preferences.getVehicleType(context).toSettings())
    val vehicleType = _vehicleType.asStateFlow()

    fun setVehicleType(vehicleType: VehicleType) {
        Preferences.setVehicleType(context, vehicleType.toPrefs())
        _vehicleType.value = vehicleType
    }

    // Map
    enum class MapType { SATELLITE, NORMAL, HYBRID }

    private var _currentMapType = MutableStateFlow(Preferences.getMapType(context).toSettings())
    val currentMapType = _currentMapType.asStateFlow()

    fun setSatelliteMap(mapType: MapType) {
        Preferences.setMapType(context, mapType.toPrefs())
        _currentMapType.value = mapType
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
}
