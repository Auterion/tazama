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

    val inserterCoords = remember {
        mutableStateOf(mutableListOf(LatLng()))
    }


    val draggedId = remember {
        mutableStateOf<Int?>(null)
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
        println("recomposing")

        key(vertice.id) {
            CircleWithItem(
                center = when (vertice.role) {
                    VerticeRole.DRAGGER -> vertice.location
                    VerticeRole.INSERTER -> DraggerCoordinateForSequence(
                        id = vertice.id,
                        vertices = vertices
                    )
                },
                radius = VerticeAtListIndex(index = index, vertices = vertices).radius,
                isDraggable = vertice.draggable,
                color = vertice.color,
                onCenterChanged = { latLng ->
                    //println("hello ${vertice.id}")
                    //println("dragged sequence ${vertice.sequence}")
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
        //}

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
fun DraggerCoordinateForSequence(id: Int, vertices: MutableList<Vertice>): LatLng {

    val sequence = vertices.find { it.id == id }?.sequence!!

    var sequencePrev = sequence - 1
    if (sequencePrev < 0) {
        sequencePrev = vertices.size - 1
    }

    var sequenceNext = sequence + 1
    if (sequenceNext > vertices.size - 1) {
        sequenceNext = 0
    }

    //println("previous sequence $sequencePrev ")

    val pixelPreve = PixelFromCoord(coord = vertices.first { it.sequence == sequencePrev }.location)
    //println("sequence next $sequenceNext")
    val pixelNext = PixelFromCoord(coord = vertices.first { it.sequence == sequenceNext }.location)

    val pixelDragger = pixelPreve + (pixelNext - pixelPreve).apply { x = x * 0.5f; y = y * 0.5f }

    return CoordFromPixel(point = pixelDragger)

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

    if (draggedId != null) {

        val currentSequence = vertices.firstOrNull { it.id == draggedId }?.sequence!!

        sequenceBefore.value = currentSequence
        var previousSequence = currentSequence - 2
        if (previousSequence < 0) {
            previousSequence = vertices.size + previousSequence
        }

        var nextSequence = currentSequence + 2
        if (nextSequence > vertices.size - 1) {
            nextSequence = nextSequence - vertices.size
        }

        val distToNext =
            ScreenDistanceBetween(
                a = vertices.first { it.sequence == currentSequence }.location,
                b = vertices.first { it.sequence == nextSequence }.location
            )

        val distToPrev =
            ScreenDistanceBetween(
                a = vertices.first { it.sequence == currentSequence }.location,
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


    if (draggedId == null && distance.value < 100.0f) {
        println("deleting the vertice!!!!!!!")
        distance.value = 500.0f
        onDeleteVertice(sequenceBefore.value)
    }
}
