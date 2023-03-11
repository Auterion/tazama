package com.auterion.tazama.util

import com.auterion.tazama.libvehicle.Distance
import com.auterion.tazama.libvehicle.PositionAbsolute
import com.auterion.tazama.libvehicle.util.GeoUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
