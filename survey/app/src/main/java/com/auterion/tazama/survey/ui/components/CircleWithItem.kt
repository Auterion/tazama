package com.auterion.tazama.survey.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.auterion.tazama.survey.R
import com.mapbox.mapboxsdk.geometry.LatLng
import org.maplibre.compose.Circle
import org.maplibre.compose.Symbol

@Composable
fun CircleWithItem(center: LatLng,
                   radius: Float,
                   draggable: Boolean,
                   color: String,
                   borderColor: String = "Black",
                   borderWidth: Float = 0.0f,
                   onCenterChanged : (LatLng) -> Unit = {}
                   ) {

    var draggableCenterState = remember {
        mutableStateOf(center)
    }

    // invisible circle, this is draggable
    Circle(center = draggableCenterState.value,
        radius = 30.0f,
        draggable = draggable,
        color = "Transparent",
        borderColor = borderColor,
        borderWidth = 0.0f,
        onCenterDragged = {
            onCenterChanged(it)
        }
        , onDragFinished = {
            draggableCenterState.value = center
        }
    )

    // display circle, this is not dragged
    Circle(center = center,
        radius = radius,
        draggable = false,
        color = color,
        borderColor = borderColor,
        borderWidth = borderWidth,
        onCenterDragged = {
        }
    )

    Circle(center = center, radius = radius, draggable = false, color = "Gray", onCenterDragged = {})
}