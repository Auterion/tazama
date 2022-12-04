
package com.auterion.tazama.data

import androidx.lifecycle.ViewModel
import com.example.tazama.data.VehicleRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class VehicleViewModel @Inject constructor(vehicleRepository: VehicleRepository) : ViewModel() {
    private val _vehiclePosition = vehicleRepository.vehiclePosition
    val vehiclePosition = _vehiclePosition
}
