package com.auterion.tazama.presentation.pages.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.auterion.tazama.R

@Composable
fun WindowDragger(
    onDragAmount: (Offset) -> Unit,
    modifier: Modifier
) {
    Surface(
        color = MaterialTheme.colors.secondary,
        border = BorderStroke(width = 1.dp, color = Color.White),
        modifier = modifier
            .size(30.dp)
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    val offsetX = dragAmount.x / density
                    val offsetY = dragAmount.y / density
                    onDragAmount(dragAmount.copy(x = offsetX, y = offsetY))
                }
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_open_in_full_24),
            contentDescription = "null",
            modifier = Modifier
                .rotate(90.0f)
                .fillMaxSize()
                .padding(5.dp)
                .clip(RoundedCornerShape(5.dp))
        )
    }
}
