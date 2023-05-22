package com.auterion.tazama.survey

import android.graphics.PointF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.graphics.minus
import androidx.core.graphics.plus
import com.mapbox.mapboxsdk.geometry.LatLng
import org.maplibre.compose.CircleWithItem
import org.maplibre.compose.CoordFromPixel
import org.maplibre.compose.CoordToPixelMaper
import org.maplibre.compose.MapLibreComposable
import org.maplibre.compose.PixelFromCoord
import org.maplibre.compose.PixelToCoordMapper
import org.maplibre.compose.PolyLine
import org.maplibre.compose.Polygon
import org.maplibre.compose.ScreenDistanceBetween

enum class VerticeRole {
    DRAGGER, INSERTER
}

data class Vertice(
    val id: Int,
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
    onDeleteVertice: (Int) -> Unit
) {

    val pointsForPolyline: MutableList<LatLng> =
        vertices.sortedBy { it.sequence }.filter { it.role == VerticeRole.DRAGGER }
            .map { it.location }.toMutableList()

    pointsForPolyline.add(vertices.first { it.sequence == 0 }.location)


    val draggedId = remember {
        mutableStateOf<Int?>(null)
    }

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
        key(vertice.id) {
            CircleWithItem(
                center = when (vertice.role) {
                    VerticeRole.DRAGGER -> vertice.location
                    VerticeRole.INSERTER -> DraggerCoordinateForId(
                        id = vertice.id,
                        vertices = vertices
                    )
                },
                radius = VerticeAtListIndex(index = index, vertices = vertices).radius,
                isDraggable = vertice.draggable,
                color = vertice.color,
                onCenterChanged = { latLng ->
                    draggedId.value = vertice.id
                    onVerticeAtIndexChanged(vertice.id, latLng)

                },
                onDragStopped = { draggedId.value = null },
                /*imageId = when (vertice.role) {
                    VerticeRole.DRAGGER -> null
                    VerticeRole.INSERTER -> R.drawable.plus
                },*/
                itemSize = 0.5f
            )
        }
    }

    VerticeDeleter(
        vertices = vertices,
        draggedId = draggedId.value,
        onDeleteVertice = onDeleteVertice
    )
}

@Composable
fun VerticeAtListIndex(index: Int, vertices: MutableList<Vertice>): Vertice {
    return vertices[index]
}

@Composable
fun DraggerCoordinateForId(id: Int, vertices: MutableList<Vertice>): LatLng {

    val sequence = vertices.find { it.id == id }?.sequence?.let { sequence ->

        val sequencePrev = (sequence - 1).WrapToListIndex(vertices.size)
        val sequenceNext = (sequence + 1).WrapToListIndex(vertices.size)

        val pixelPreve =
            PixelFromCoord(coord = vertices.first { it.sequence == sequencePrev }.location)
        val pixelNext =
            PixelFromCoord(coord = vertices.first { it.sequence == sequenceNext }.location)

        val pixelDragger =
            pixelPreve + (pixelNext - pixelPreve).apply { x = x * 0.5f; y = y * 0.5f }

        return CoordFromPixel(point = pixelDragger)
    }
    return LatLng()
}

@Composable
fun VerticeDeleter(
    vertices: MutableList<Vertice>,
    draggedId: Int?,
    onDeleteVertice: (sequence: Int) -> Unit
) {

    val distance = remember {
        mutableStateOf(500.0f)
    }

    val sequenceBefore = remember {
        mutableStateOf(-1)
    }

    draggedId?.let { draggedId ->

        vertices.firstOrNull { it.id == draggedId }?.let { vertice ->

            sequenceBefore.value = vertice.sequence
            val previousSequence = (vertice.sequence - 2).WrapToListIndex(vertices.size)
            val nextSequence = (vertice.sequence + 2).WrapToListIndex(vertices.size)

            val distToNext =
                ScreenDistanceBetween(
                    a = vertices.first { it.sequence == vertice.sequence }.location,
                    b = vertices.first { it.sequence == nextSequence }.location
                )

            val distToPrev =
                ScreenDistanceBetween(
                    a = vertices.first { it.sequence == vertice.sequence }.location,
                    b = vertices.first { it.sequence == previousSequence }.location
                )

            var seqToRemove = 0

            if (distToNext < distToPrev) {
                distance.value = distToNext
                seqToRemove = nextSequence

            } else {
                distance.value = distToPrev
                seqToRemove = previousSequence
            }

            if (distance.value < 100.0f) {
                CircleWithItem(
                    center = vertices.first { it.sequence == seqToRemove }.location,
                    radius = 50.0f,
                    opacity = 0.5f,
                    isDraggable = false,
                    color = "Orange"
                )
            }
        }
    }

    if (draggedId == null && distance.value < 100.0f) {
        distance.value = Float.POSITIVE_INFINITY
        onDeleteVertice(sequenceBefore.value)
    }
}
