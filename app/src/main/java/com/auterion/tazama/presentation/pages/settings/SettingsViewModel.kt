package com.auterion.tazama.presentation.pages.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    private val _fakeVehiclePosition = MutableStateFlow(false)
    val fakeVehiclePosition = _fakeVehiclePosition.asStateFlow()

    private val _mapTypes = listOf("Satellite", "Normal", "Hybrid")
    val mapTypes = _mapTypes

    private var _currentMapType = MutableStateFlow("Satellite")
    val currentMapType = _currentMapType.asStateFlow()

    fun setFakeVehiclePosition(fake: Boolean) {
        _fakeVehiclePosition.value = fake
    }

    fun setSatelliteMap(mapType: String) {
        if (_mapTypes.contains(mapType)) {
            _currentMapType.value = mapType
        }
    }
}