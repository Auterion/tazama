/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package com.auterion.tazama.libui.presentation.pages.main

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun SwappableView(
    modifier: Modifier = Modifier,
    overlayModifier: Modifier = Modifier,
    overlayAlignment: Alignment = Alignment.TopStart,
    overlayAspectRatio: Float? = null,
    view1: (@Composable () -> Unit)? = null,
    view2: (@Composable () -> Unit)? = null,
) {
    var isOverlayView1 by remember { mutableStateOf(true) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        val extraModifier = overlayAspectRatio?.let { Modifier.aspectRatio(it) } ?: Modifier

        Box(
            modifier = Modifier
                .zIndex(1F)
                .align(overlayAlignment)
                .then(extraModifier)
        ) {
            ResizableBox(
                modifier = Modifier
                    .then(overlayModifier)
                    .align(overlayAlignment)
                    .pointerInput(Unit) {
                        interceptTap(onTap = {
                            isOverlayView1 = !isOverlayView1
                        })
                    },
                alignment = overlayAlignment,
                ratio = overlayAspectRatio
            ) {
                if (isOverlayView1) view1?.invoke() else view2?.invoke()
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0F)
        ) {
            if (isOverlayView1) view2?.invoke() else view1?.invoke()
        }
    }
}

suspend fun PointerInputScope.interceptTap(
    pass: PointerEventPass = PointerEventPass.Initial,
    onTap: ((Offset) -> Unit)? = null,
) = coroutineScope {
    if (onTap == null) return@coroutineScope

    var doubleTapClock: Job? = null

    awaitEachGesture {
        val down = awaitFirstDown(pass = pass)
        val downTime = System.currentTimeMillis()
        val tapTimeout = viewConfiguration.longPressTimeoutMillis
        val tapPosition = down.position
        var candidateUp: PointerInputChange? = null

        if (doubleTapClock?.isActive == true) {
            // A doubleTapClock is active, meaning that a tap is waiting for confirmation (i.e.
            // that it is not becoming a double tap). But this happens after `awaitFirstDown`,
            // meaning that we just received a second "down" even that makes it a double tap.
            // Therefore we cancel it and let the events go through.
            doubleTapClock?.cancel()
            return@awaitEachGesture
        }

        doubleTapClock = launch {
            delay(viewConfiguration.doubleTapTimeoutMillis)

            if (!isActive) return@launch

            candidateUp?.let { up ->
                down.consume()
                up.consume()
                candidateUp = null
                onTap(tapPosition)
            }
        }

        do {
            val event = awaitPointerEvent(pass)
            val currentTime = System.currentTimeMillis()

            if (candidateUp != null) {
                // New event before end: not a tap
                doubleTapClock?.cancel()
                break
            }

            if (event.changes.size != 1) {
                // More than one event: not a tap
                doubleTapClock?.cancel()
                break
            }

            if (currentTime - downTime >= tapTimeout) {
                // Too slow: not a tap
                doubleTapClock?.cancel()
                break
            }

            val change = event.changes[0]
            if (change.id != down.id) {
                // Event is not for the current tap: not a tap
                doubleTapClock?.cancel()
                break
            }

            if ((change.position - tapPosition).getDistance() > viewConfiguration.touchSlop) {
                // Too much movement: not a tap
                doubleTapClock?.cancel()
                break
            }

            if (change.id == down.id && !change.pressed) {
                candidateUp = change
            }
        } while (change.pressed)
    }
}
