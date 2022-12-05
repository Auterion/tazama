package com.auterion.tazama.data

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

class VehicleDummyImpl : Vehicle {
    override  val vehiclePosition = MutableStateFlow<LatLng>(LatLng(0.0,0.0))

    init {
        // this simulates the vehicle sending it's position at 1Hz
        CoroutineScope(Job() + Dispatchers.IO).launch {
            while(true) {
                vehiclePosition.value = LatLng(Random.nextDouble(3.0, 4.0), Random.nextDouble(46.0, 47.0))
                delay(1000)
            }
        }
    }
}
