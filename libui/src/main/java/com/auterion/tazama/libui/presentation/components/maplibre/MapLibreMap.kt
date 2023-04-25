package com.auterion.tazama.libui.presentation.components.maplibre


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableTargetMarker
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.Circle
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import kotlinx.coroutines.delay

@Retention(AnnotationRetention.BINARY)
@ComposableTargetMarker(description = "Maplibre Composable")
@Target(
    AnnotationTarget.FILE,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER,
)
public annotation class MapLibreComposable

@Composable
fun MapLibre(
    modifier: Modifier,
    content: (@Composable @MapLibreComposable () -> Unit)? = null,
) {
    if (LocalInspectionMode.current) {
        Box(modifier = modifier)
        return
    }

    LocalContext.current
    val map = rememberMapViewWithLifecycle()

    val key = "2z0TwvuXjwgOpvle5GYY"
    var symbol: Symbol? = remember { null }
    var circle: Circle? = remember { null }

    val currentContent by rememberUpdatedState(content)

    var style: Style? = remember { null }


    val parentComposition = rememberCompositionContext()

    AndroidView(modifier = Modifier.fillMaxSize(), factory = { map })
    LaunchedEffect(Unit) {
        val libreMap = map.awaitMap()
        Helper.validateKey(key)
        val styleUrl = "https://api.maptiler.com/maps/satellite/style.json?key=${key}";
        libreMap.setStyle(styleUrl) { style1 ->
            style = style1

        }


        disposingComposition {
            delay(2000)
            map.newComposition(parentComposition, style = libreMap.style!!) {
                CompositionLocalProvider() {
                    currentContent?.invoke()
                }
            }
        }

    }
}