package com.auterion.tazama.survey

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.graphics.minus
import androidx.core.graphics.plus
import com.mapbox.mapboxsdk.geometry.LatLng
import org.maplibre.compose.CircleWithItem
import org.maplibre.compose.MapLibreComposable
import org.maplibre.compose.MapObserver
import org.maplibre.compose.PolyLine
import org.maplibre.compose.Polygon
import org.maplibre.compose.coordFromPixel
import org.maplibre.compose.pixelFromCoord
import org.maplibre.compose.screenDistanceBetween

enum class VertexRole {
    DRAGGER, INSERTER
}

data class Vertex(
    val id: Int,
    val location: LatLng,
    val radius: Float,
    val draggable: Boolean = false,
    val color: String = "Black",
    val role: VertexRole = VertexRole.DRAGGER,
    val sequence: Int
)

@MapLibreComposable
@Composable
fun SurveyPolygon(
    vertices: MutableList<Vertex>,
    onVerticesTranslated: (MutableList<LatLng>) -> Unit,
    onVertexWithIdChanged: (Int, LatLng) -> Unit,
    onDeleteVertex: (Int) -> Unit,
) {

    val lastMapUpdateMs = remember {
        mutableStateOf(System.currentTimeMillis())
    }

    val pointsForPolyline: MutableList<LatLng> = vertices
        .sortedBy { it.sequence }
        .filter { it.role == VertexRole.DRAGGER }
        .map { it.location }.toMutableList()

    pointsForPolyline.add(vertices.first { it.sequence == 0 }.location)


    val draggedId = remember { mutableStateOf<Int?>(null) }

    val mapScaleChangeToggler = remember {
        mutableStateOf(true)
    }

    MapObserver(onMapScaled = {
        if (System.currentTimeMillis() - lastMapUpdateMs.value > 100) {
            lastMapUpdateMs.value = System.currentTimeMillis()
            mapScaleChangeToggler.value = !mapScaleChangeToggler.value
        }
    })

    Polygon(
        vertices = pointsForPolyline,
        draggerImageId = R.drawable.drag,
        fillColor = "Green",
        opacity = 0.2f,
        isDraggable = true,
        onVerticesChanged = { onVerticesTranslated(it.first()) }
    )

    PolyLine(
        points = pointsForPolyline,
        color = "Black",
        lineWidth = 2.0f
    )

    key(mapScaleChangeToggler.value) {  // this triggers recomposition when map is scaled
        vertices.forEachIndexed { index, vertex ->
            key(vertex.id) {
                if (vertex.role == VertexRole.DRAGGER || isEnoughSpaceForInserter(
                        id = vertex.id,
                        vertices = vertices
                    )
                )
                    CircleWithItem(
                        center = when (vertex.role) {
                            VertexRole.DRAGGER -> vertex.location
                            VertexRole.INSERTER -> inserterCoordinateForId(
                                id = vertex.id,
                                vertices = vertices
                            )
                        },
                        radius = vertices[index].radius,
                        isDraggable = vertex.draggable,
                        color = vertex.color,
                        onCenterChanged = { latLng ->
                            draggedId.value = vertex.id
                            onVertexWithIdChanged(vertex.id, latLng)

                        },
                        onDragStopped = { draggedId.value = null },
                        imageId = when (vertex.role) {
                            VertexRole.DRAGGER -> null
                            VertexRole.INSERTER -> R.drawable.plus
                        },
                        itemSize = 0.5f,
                    )
            }
        }
    }

    VertexDeleter(
        vertices = vertices,
        draggedId = draggedId.value,
        onDeleteVertex = onDeleteVertex
    )
}

@Composable
fun isEnoughSpaceForInserter(id: Int, vertices: MutableList<Vertex>): Boolean {
    vertices.firstOrNull {
        it.id == id
    }?.sequence?.let { inserterSequence ->
        val prevVertex =
            vertices.find { it.sequence == (inserterSequence - 1).WrapToListIndex(vertices.size) }
        val nextVertex =
            vertices.find { it.sequence == (inserterSequence + 1).WrapToListIndex(vertices.size) }

        val dist = screenDistanceBetween(a = prevVertex?.location!!, b = nextVertex?.location!!)

        return dist > 400
    }

    return false
}

@Composable
fun inserterCoordinateForId(id: Int, vertices: MutableList<Vertex>): LatLng {
    vertices.find { it.id == id }?.sequence?.let { sequence ->
        val sequencePrev = (sequence - 1).WrapToListIndex(vertices.size)
        val sequenceNext = (sequence + 1).WrapToListIndex(vertices.size)

        val pixelPrev = pixelFromCoord(vertices.first { it.sequence == sequencePrev }.location)
        val pixelNext = pixelFromCoord(vertices.first { it.sequence == sequenceNext }.location)

        val pixelDragger = pixelPrev + (pixelNext - pixelPrev).apply { x *= 0.5f; y *= 0.5f }

        return coordFromPixel(point = pixelDragger)
    }

    return LatLng()
}

@Composable
fun VertexDeleter(
    vertices: MutableList<Vertex>,
    draggedId: Int?,
    onDeleteVertex: (sequence: Int) -> Unit
) {
    val distance = remember { mutableStateOf(Float.POSITIVE_INFINITY) }

    val sequenceBefore = remember {
        mutableStateOf(-1)
    }

    draggedId?.let {
        vertices.firstOrNull { it.id == draggedId }?.let { vertex ->
            sequenceBefore.value = vertex.sequence
            val previousSequence = (vertex.sequence - 2).WrapToListIndex(vertices.size)
            val nextSequence = (vertex.sequence + 2).WrapToListIndex(vertices.size)

            val distToNext =
                screenDistanceBetween(
                    a = vertices.first { it.sequence == vertex.sequence }.location,
                    b = vertices.first { it.sequence == nextSequence }.location
                )

            val distToPrev =
                screenDistanceBetween(
                    a = vertices.first { it.sequence == vertex.sequence }.location,
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
        onDeleteVertex(sequenceBefore.value)
    }
}
