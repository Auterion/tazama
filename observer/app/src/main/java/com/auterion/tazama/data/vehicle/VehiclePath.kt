package com.auterion.tazama.data.vehicle

import com.auterion.tazama.libvehicle.Distance
import com.auterion.tazama.libvehicle.PositionAbsolute
import com.auterion.tazama.util.distinctUntil
import com.auterion.tazama.util.windowed
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

class VehiclePath(
    private val position: Flow<PositionAbsolute?>,
    private val minDistance: Distance = Distance(1.0),
    private val pathMaxLength: Int = 5000,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = Job() + ioDispatcher
    var relayJob: Job? = null

    private val _path = MutableStateFlow<List<LatLng>>(listOf())
    val path = _path.asStateFlow()

    init {
        relayJob = linkFlows()
    }

    private fun linkFlows(): Job {
        return launch {
            position
                .filterNotNull()
                .distinctUntil(minDistance)
                .map { LatLng(it.lat.value, it.lon.value) }
                .windowed(pathMaxLength)
                .collect { elem ->
                    _path.emit(elem)
                }
        }
    }

    fun clear() {
        relayJob?.cancel()
        launch {
            _path.emit(listOf())
            relayJob = linkFlows()
        }
    }
}
