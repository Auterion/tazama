package com.auterion.tazama.data

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow

interface Vehicle {
    var vehiclePosition : MutableStateFlow<LatLng>
}
