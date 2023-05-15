package com.auterion.tazama.survey

import android.graphics.PointF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.graphics.minus
import androidx.core.graphics.plus
import com.mapbox.mapboxsdk.geometry.LatLng
import org.maplibre.compose.CircleWithItem
import org.maplibre.compose.CoordToPixelMaper
import org.maplibre.compose.MapLibreComposable
import org.maplibre.compose.PixelToCoordMapper
import org.maplibre.compose.PolyLine
import org.maplibre.compose.Polygon

enum class VerticeRole {
    DRAGGER, INSERTER
}

data class Vertice(
    val location: LatLng,
    val radius: Float,
    val draggable: Boolean = false,
    val color: String = "Black",
    val role: VerticeRole = VerticeRole.DRAGGER,
    val sequence: Int,
)


@Composable
fun InsertPointsCalculator(coords: List<LatLng>, onChange: (List<LatLng>) -> Unit) {
    val pixelCoordinates = remember {
        mutableStateOf(mutableListOf(PointF()))
    }
    CoordToPixelMaper(
        coordinates = coords.toMutableList(),
        onChange = { pixelCoordinates.value = it.toMutableList() })


    val points = mutableListOf<PointF>()

    pixelCoordinates.value.forEachIndexed { index, point ->
        var tmp = PointF()
        if (index < pixelCoordinates.value.size - 1) {
            tmp = (pixelCoordinates.value[index + 1] - point)
        } else {
            // last item
            tmp = pixelCoordinates.value[0] - point
        }
        tmp.x = tmp.x * 0.5f
        tmp.y = tmp.y * 0.5f
        points.add(point + tmp)
    }

    PixelToCoordMapper(points = points, onChange = { onChange(it) })

}

@MapLibreComposable
@Composable
fun SurveyPolygon(
    vertices: MutableList<Vertice>,
    onVerticesTranslated: (MutableList<LatLng>) -> Unit,
    onVerticeAtIndexChanged: (Int, LatLng) -> Unit,
) {

    val pointsForPolyline: MutableList<LatLng> =
        vertices.sortedBy { it.sequence }.filter { it.role == VerticeRole.DRAGGER }
            .map { it.location }.toMutableList()

    pointsForPolyline.add(vertices.first().location)

    val inserterCoords = remember {
        mutableStateOf(mutableListOf(LatLng()))
    }


    InsertPointsCalculator(coords = vertices.sortedBy { it.sequence }
        .filter { it.role == VerticeRole.DRAGGER }
        .map {
            it.location
        }, onChange = { inserterCoords.value = it.toMutableList() }
    )

    Polygon(
        vertices = mutableListOf(
            vertices.sortedBy { it.sequence }.filter { it.role == VerticeRole.DRAGGER }
                .map { it.location }.toMutableList()
        ),
        fillColor = "Green",
        opacity = 0.3f,
        isDraggable = true,
        onVerticesChanged = {
            onVerticesTranslated(it.first())

        })
    PolyLine(
        points = pointsForPolyline,
        color = "Black",
        lineWidth = 2.0f
    )

    vertices.forEachIndexed { index, vertice ->
        CircleWithItem(
            center = when (vertice.role) {
                VerticeRole.DRAGGER -> vertice.location
                VerticeRole.INSERTER -> inserterCoords.value[vertice.sequence / 2]
            },
            radius = vertice.radius,
            isDraggable = vertice.draggable,
            color = when (vertice.role) {
                VerticeRole.DRAGGER -> "Gray"
                VerticeRole.INSERTER -> "Red"
            },
            onCenterChanged = { latLng ->
                onVerticeAtIndexChanged(index, latLng)
            },
            text = vertice.sequence.toString(),
            itemSize = 12.0f
        )

    }
}