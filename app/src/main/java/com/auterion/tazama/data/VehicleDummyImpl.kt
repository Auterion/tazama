package com.auterion.tazama.data

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.random.Random

class VehicleDummyImpl @Inject constructor() : Vehicle {
    override var vehiclePosition = MutableStateFlow(LatLng(0.0, 0.0))

    init {
        // This simulates the vehicle sending it's position at 1Hz
        CoroutineScope(Job() + Dispatchers.IO).launch {
            while (true) {
                println("emitting stuff into this value")
                vehiclePosition.value =
                    LatLng(Random.nextDouble(3.0, 4.0), Random.nextDouble(46.0, 47.0))
                delay(1000)
            }
        }
    }
}
