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
import org.maplibre.compose.ScreenDistanceBetween

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
    onDeleteVertice: (Int) -> Unit
) {

    val pointsForPolyline: MutableList<LatLng> =
        vertices.sortedBy { it.sequence }.filter { it.role == VerticeRole.DRAGGER }
            .map { it.location }.toMutableList()

    pointsForPolyline.add(vertices.first().location)

    val inserterCoords = remember {
        mutableStateOf(mutableListOf(LatLng()))
    }

    val draggedVerticeSequence = remember {
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

    //vertices.forEachIndexed { index, vertice ->
    repeat(vertices.size) {
        val vertice = vertices[it]
        val index = it
        CircleWithItem(
            center = when (vertice.role) {
                VerticeRole.DRAGGER -> vertice.location
                VerticeRole.INSERTER -> inserterCoords.value[vertice.sequence / 2]
            },
            radius = vertice.radius,
            isDraggable = vertice.draggable,
            color = vertice.color,
            onCenterChanged = { latLng ->
                println("size of vertices ${vertices.size}")
                onVerticeAtIndexChanged(index, latLng)
                draggedVerticeSequence.value = vertices[index].sequence
            },
            onDragStopped = { draggedVerticeSequence.value = null },
            imageId = when (vertice.role) {
                VerticeRole.DRAGGER -> null
                VerticeRole.INSERTER -> R.drawable.plus
            },
            itemSize = 0.5f
        )

    }

    VerticeDeleter(
        vertices = vertices,
        draggedSequence = draggedVerticeSequence.value,
        onDeleteVertice = onDeleteVertice
    )


}

@Composable
fun VerticeDeleter(
    vertices: MutableList<Vertice>,
    draggedSequence: Int?,
    onDeleteVertice: (sequence: Int) -> Unit
) {

    val distance = remember {
        mutableStateOf(500.0f)
    }

    val sequenceBefore = remember {
        mutableStateOf(-1)
    }

    draggedSequence?.let {
        sequenceBefore.value = draggedSequence
        var previousSequence = draggedSequence - 2
        if (previousSequence < 0) {
            previousSequence = vertices.size + previousSequence
        }

        distance.value =
            ScreenDistanceBetween(
                a = vertices.first { it.sequence == draggedSequence }.location,
                b = vertices.first { it.sequence == previousSequence }.location
            )

        println("distance " + distance.value)

        if (distance.value < 200.0f) {
            CircleWithItem(
                center = vertices.first { it.sequence == previousSequence }.location,
                radius = 50.0f,
                opacity = 0.5f,
                isDraggable = false,
                color = "Orange"
            )
        }


    }

    if (draggedSequence == null && distance.value < 100.0f) {
        println("deleting the vertice!!!!!!!")
        distance.value = 500.0f
        onDeleteVertice(sequenceBefore.value)
    }
}