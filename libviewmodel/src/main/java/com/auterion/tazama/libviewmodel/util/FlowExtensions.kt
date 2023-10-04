/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package com.auterion.tazama.libviewmodel.util

import com.auterion.tazama.libvehicle.Distance
import com.auterion.tazama.libvehicle.PositionAbsolute
import com.auterion.tazama.libvehicle.util.GeoUtils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

fun Flow<PositionAbsolute>.distinctUntil(minDist: Distance): Flow<PositionAbsolute> = flow {
    var previous: PositionAbsolute? = null
    collect { element ->
        if (previous == null || GeoUtils.distanceBetween(previous!!, element) > minDist) {
            previous = element
            emit(element)
        }
    }
}

fun <T> Flow<T>.windowed(size: Int): Flow<List<T>> = flow {
    val cache = ArrayDeque<T>(size)
    collect { element ->
        if (cache.size == size) cache.removeFirst()
        cache.add(element)
        emit(cache.toList())
    }
}

private class DerivedStateFlow<T>(
    private val getValue: () -> T,
    private val flow: Flow<T>
) : StateFlow<T> {
    override val replayCache: List<T>
        get() = listOf(value)

    override val value: T
        get() = getValue()

    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        coroutineScope { flow.distinctUntilChanged().stateIn(this).collect(collector) }
    }
}

fun <T1, R> StateFlow<T1>.mapState(transform: (a: T1) -> R): StateFlow<R> {
    return DerivedStateFlow(
        getValue = { transform(this.value) },
        flow = this.map { a -> transform(a) }
    )
}
