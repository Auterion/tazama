package com.auterion.tazama.presentation.pages.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val dataStore: DataStore<Preferences>) :
    ViewModel() {
    // Vehicle
    enum class VehicleType { FAKE, MAVSDK }

    private val _vehicleType = MutableStateFlow(VehicleType.FAKE)
    val vehicleType = _vehicleType.asStateFlow()

    fun setVehicleType(vehicleType: VehicleType) {
        _vehicleType.value = vehicleType
    }

    // Map
    private val _mapTypes = listOf("Satellite", "Normal", "Hybrid")
    val mapTypes = _mapTypes

    private var _currentMapType = MutableStateFlow("Satellite")
    val currentMapType = _currentMapType.asStateFlow()

    fun setSatelliteMap(mapType: String) {
        if (_mapTypes.contains(mapType)) {
            _currentMapType.value = mapType
        }
    }
}