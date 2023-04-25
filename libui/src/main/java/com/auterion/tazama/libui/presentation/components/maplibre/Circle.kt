package com.auterion.tazama.libui.presentation.components.maplibre
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import com.auterion.tazama.libui.presentation.components.maplibre.CircleNode
import com.auterion.tazama.libui.presentation.components.maplibre.MapApplier
import com.auterion.tazama.libui.presentation.components.maplibre.MapLibreComposable
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.annotation.CircleManager
import com.mapbox.mapboxsdk.plugins.annotation.CircleOptions

@Composable
@MapLibreComposable
fun Circle() {
    val mapApplier = currentComposer.applier as? MapApplier

    ComposeNode<CircleNode, MapApplier>(factory = {
        val circleManager =
            CircleManager(mapApplier?.mapView!!, mapApplier?.map!!, mapApplier?.style!!)

        val circleOptions =
            CircleOptions().withCircleRadius(30.0f)
                .withLatLng(LatLng(-6.8, 39.2)).withDraggable(true)

        val circle = circleManager.create(circleOptions)
        CircleNode(circle) {

        }
    }, update = {}) {

    }
}
