
package com.auterion.tazama.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auterion.tazama.data.VehicleRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class VehicleViewModel @Inject constructor(vehicleRepository: VehicleRepository) : ViewModel() {
    private var _vehiclePosition = vehicleRepository.vehicle.vehiclePosition
    var vehiclePosition = _vehiclePosition
}
