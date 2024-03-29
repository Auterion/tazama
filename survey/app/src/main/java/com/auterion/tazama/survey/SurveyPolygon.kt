/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package com.auterion.tazama.survey

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.graphics.minus
import androidx.core.graphics.plus
import com.mapbox.mapboxsdk.geometry.LatLng
import org.ramani.compose.CircleWithItem
import org.ramani.compose.MapLibreComposable
import org.ramani.compose.MapObserver
import org.ramani.compose.Polygon
import org.ramani.compose.Polyline
import org.ramani.compose.Symbol
import org.ramani.compose.coordFromPixel
import org.ramani.compose.pixelFromCoord
import org.ramani.compose.screenDistanceBetween
import kotlin.math.PI

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
    vertices: List<Vertex>,
    transects: List<Transect>,
    azimuth: Float,
    onVerticesTranslated: (List<LatLng>) -> Unit,
    onVertexWithIdChanged: (Int, LatLng) -> Unit,
    onDeleteVertex: (Int) -> Unit,
    onGridAngleChanged: (Float) -> Unit,
) {
    val lastMapUpdateMs = remember {
        mutableStateOf(System.currentTimeMillis())
    }

    val pointsForPolyline: MutableList<LatLng> = vertices
        .sortedBy { it.sequence }
        .filter { it.role == VertexRole.DRAGGER }
        .map { it.location }.toMutableList()

    val draggedId = remember { mutableStateOf<Int?>(null) }
    val mapScaleChangeToggler = remember { mutableStateOf(true) }
    val polygonZIndex = 0

    val mapBearing = remember {
        mutableStateOf(0.0)
    }

    MapObserver(onMapScaled = {
        if (System.currentTimeMillis() - lastMapUpdateMs.value > 100) {
            lastMapUpdateMs.value = System.currentTimeMillis()
            mapScaleChangeToggler.value = !mapScaleChangeToggler.value
        }

    },
        onMapRotated = {
            mapBearing.value = it
        })

    Polygon(
        vertices = pointsForPolyline,
        azimuth = azimuth,
        draggerImageId = R.drawable.drag,
        fillColor = "Green",
        opacity = 0.2f,
        isDraggable = true,
        borderWidth = 0.5F,
        zIndex = polygonZIndex,
        zIndexDragHandle = 20,
        zIndexRotationHandle = 5,
        onVerticesChanged = { onVerticesTranslated(it) },
        onAzimuthChanged = onGridAngleChanged,
    )

    key(mapScaleChangeToggler.value) {  // this triggers recomposition when map is scaled
        vertices.forEachIndexed { index, vertex ->
            key(vertex.id) {
                if (vertex.role == VertexRole.DRAGGER || hasEnoughSpaceForInserter(
                        id = vertex.id,
                        vertices = vertices
                    )
                ) {
                    CircleWithItem(
                        center = when (vertex.role) {
                            VertexRole.DRAGGER -> vertex.location
                            VertexRole.INSERTER -> inserterCoordinateForId(
                                id = vertex.id,
                                vertices = vertices
                            )
                        },
                        radius = vertices[index].radius,
                        dragRadius = 40.0f,
                        isDraggable = vertex.draggable,
                        color = vertex.color,
                        borderColor = "Black",
                        borderWidth = 1.0f,
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
                        zIndex = 10
                    )
                }
            }
        }
    }

    VertexDeleter(
        vertices = vertices,
        draggedId = draggedId.value,
        onDeleteVertex = onDeleteVertex
    )

    transects.forEach {
        Polyline(points = listOf(it.startLat, it.endLat), color = "White", lineWidth = 1.0f)
    }

    transects.windowed(2, 1) {
        listOf(it.first().endLat, it.last().startLat)
    }.forEach {
        Polyline(
            points = it,
            color = "White",
            lineWidth = 1.0f,
            isDashed = true,
            zIndex = 2
        )
    }

    if (transects.isNotEmpty()) {
        ArrowOnTransect(transects.first(), mapBearing.value)
    }

    if (transects.size > 1) {
        ArrowOnTransect(transects.last(), mapBearing.value)
    }
}

@Composable
private fun ArrowOnTransect(
    transect: Transect,
    mapBearing: Double,
) {
    Symbol(
        center = transect.directionIndicator.position,
        size = 0.5f,
        color = "White",
        isDraggable = false,
        imageId = R.drawable.arrow_right_white,
        imageRotation = transect.directionIndicator.rotation * 180 / PI.toFloat() - mapBearing.toFloat(),
        zIndex = 12,
    )
}

@Composable
private fun hasEnoughSpaceForInserter(id: Int, vertices: List<Vertex>): Boolean {
    vertices.firstOrNull {
        it.id == id
    }?.sequence?.let { inserterSequence ->
        val prevVertex =
            vertices.find { it.sequence == (inserterSequence - 1).wrapToListIndex(vertices.size) }
        val nextVertex =
            vertices.find { it.sequence == (inserterSequence + 1).wrapToListIndex(vertices.size) }

        val dist = screenDistanceBetween(a = prevVertex!!.location, b = nextVertex!!.location)

        return dist > 400
    }

    return false
}

@Composable
private fun inserterCoordinateForId(id: Int, vertices: List<Vertex>): LatLng {
    vertices.find { it.id == id }?.sequence?.let { sequence ->
        val sequencePrev = (sequence - 1).wrapToListIndex(vertices.size)
        val sequenceNext = (sequence + 1).wrapToListIndex(vertices.size)

        val pixelPrev = pixelFromCoord(vertices.first { it.sequence == sequencePrev }.location)
        val pixelNext = pixelFromCoord(vertices.first { it.sequence == sequenceNext }.location)

        val pixelDragger = pixelPrev + (pixelNext - pixelPrev).apply { x *= 0.5f; y *= 0.5f }

        return coordFromPixel(point = pixelDragger)
    }

    return LatLng()
}

@Composable
private fun VertexDeleter(
    vertices: List<Vertex>,
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
            val previousSequence = (vertex.sequence - 2).wrapToListIndex(vertices.size)
            val nextSequence = (vertex.sequence + 2).wrapToListIndex(vertices.size)

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

            val seqToRemove = if (distToNext < distToPrev) {
                distance.value = distToNext
                nextSequence
            } else {
                distance.value = distToPrev
                previousSequence
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
