/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package com.auterion.tazama.libviewmodel.vehicle

import com.auterion.tazama.libvehicle.Distance
import com.auterion.tazama.libvehicle.PositionAbsolute
import com.auterion.tazama.libviewmodel.util.distinctUntil
import com.auterion.tazama.libviewmodel.util.windowed
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

data class LatLng(val latitude: Double, val longitude: Double)

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
