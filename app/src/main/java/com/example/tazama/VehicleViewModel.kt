package com.example.tazama

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class VehicleViewModel @Inject constructor() : ViewModel() {
    private val _vehiclePosition = MutableStateFlow(LatLng(0.0, 0.0))
    val vehiclePosition = _vehiclePosition.asStateFlow()

    fun setRandomVehiclePosition() {
        val randLatLon = LatLng(Random.nextDouble(-90.0, 90.0), Random.nextDouble(-90.0, 90.0))
        _vehiclePosition.value = randLatLon
    }
}