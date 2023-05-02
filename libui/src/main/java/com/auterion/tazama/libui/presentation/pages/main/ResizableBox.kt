package com.auterion.tazama.libui.presentation.pages.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Composable
fun ResizableBox(
    modifier: Modifier = Modifier,
    initialSize: DpSize = DpSize(200.dp, 112.5.dp),
    alignment: Alignment = Alignment.TopStart,
    ratio: Float? = null,
    content: (@Composable () -> Unit)? = null,
) {
    var size by remember { mutableStateOf(initialSize) }

    Box(
        modifier = Modifier
            .then(modifier)
            .size(size)
    ) {
        content?.invoke()

        val draggerAlignment = when (alignment) {
            Alignment.TopStart -> Alignment.BottomEnd
            Alignment.TopEnd -> Alignment.BottomStart
            Alignment.BottomStart -> Alignment.TopEnd
            Alignment.BottomEnd -> Alignment.TopStart
            else -> throw RuntimeException("Unsupported alignment")
        }

        val handleRotation = when (alignment) {
            Alignment.TopStart -> 90F
            Alignment.TopEnd -> 180F
            Alignment.BottomStart -> 0F
            Alignment.BottomEnd -> 270F
            else -> throw RuntimeException("Unsupported alignment")
        }

        WindowDragger(
            modifier = Modifier.align(draggerAlignment),
            rotation = handleRotation,
        ) {
            size = when (alignment) {
                Alignment.TopStart -> {
                    val newWidth = size.width + it.x.dp
                    val newHeight = size.height + it.y.dp
                    DpSize(newWidth, ratio?.let { newWidth / it } ?: newHeight)
                }

                Alignment.TopEnd -> {
                    val newWidth = size.width - it.x.dp
                    val newHeight = size.height + it.y.dp
                    DpSize(newWidth, ratio?.let { newWidth / it } ?: newHeight)
                }

                Alignment.BottomStart -> {
                    val newWidth = size.width + it.x.dp
                    val newHeight = size.height - it.y.dp
                    DpSize(newWidth, ratio?.let { newWidth / it } ?: newHeight)
                }

                Alignment.BottomEnd -> {
                    val newWidth = size.width - it.x.dp
                    val newHeight = size.height - it.y.dp
                    DpSize(newWidth, ratio?.let { newWidth / it } ?: newHeight)
                }

                else -> throw RuntimeException("Unsupported alignment")
            }
        }
    }
}
