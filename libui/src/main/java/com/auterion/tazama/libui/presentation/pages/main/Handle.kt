/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package com.auterion.tazama.libui.presentation.pages.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.auterion.tazama.libui.R

@Composable
fun Handle(
    modifier: Modifier,
    color: Color,
    border: BorderStroke,
    onDragAmount: (Offset) -> Unit,
    content: (@Composable () -> Unit)? = null,
) {
    val currentContent by rememberUpdatedState(content)

    Surface(
        color = color,
        border = border,
        modifier = Modifier
            .size(30.dp)
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    val offsetX = dragAmount.x / density
                    val offsetY = dragAmount.y / density
                    onDragAmount(dragAmount.copy(x = offsetX, y = offsetY))
                }
            }
            .then(modifier)
    ) {
        currentContent?.invoke()
    }
}

@Composable
fun WindowDragger(
    modifier: Modifier,
    color: Color = MaterialTheme.colors.secondary,
    border: BorderStroke = BorderStroke(width = 1.dp, color = Color.White),
    rotation: Float = 90.0F,
    onDragAmount: (Offset) -> Unit,
) {
    Handle(modifier, color, border, onDragAmount) {
        Image(
            painter = painterResource(id = R.drawable.baseline_open_in_full_24),
            contentDescription = "null",
            modifier = Modifier
                .rotate(rotation)
                .fillMaxSize()
                .padding(5.dp)
        )
    }
}
