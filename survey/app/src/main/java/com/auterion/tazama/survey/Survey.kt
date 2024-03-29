/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package com.auterion.tazama.survey

import androidx.compose.runtime.mutableStateListOf
import com.auterion.tazama.survey.utils.geo.BoundingRectangleCorners
import com.auterion.tazama.survey.utils.geo.BoundingRectanglePolygon
import com.auterion.tazama.survey.utils.geo.Line
import com.auterion.tazama.survey.utils.geo.LineIntersectionPoint
import com.auterion.tazama.survey.utils.geo.LocalProjection
import com.auterion.tazama.survey.utils.geo.PointF
import com.auterion.tazama.survey.utils.geo.Polygon
import com.auterion.tazama.survey.utils.geo.rotateAroundCenter
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt


class Survey : CoroutineScope {
    override val coroutineContext: CoroutineContext = Job() + Dispatchers.Main
    private var vertices = mutableStateListOf<Vertex>()
    private var _verticesFlow = MutableStateFlow(vertices.toList())
    val verticesFlow = _verticesFlow.asStateFlow()

    private val _transectFlow = MutableStateFlow(emptyList<Transect>())
    val transectFlow = _transectFlow.asStateFlow()

    private val _angleFlow = MutableStateFlow(0.0f)
    val angleFlow = _angleFlow.asStateFlow()

    private val _transectSpacingFlow = MutableStateFlow(50.0f)
    val transectSpacingFlow = _transectSpacingFlow.asStateFlow()
    val minSpacing = 1.0f
    val maxSpacing = 500.0f

    val transectMargin = 10.0f

    var vertexId = 8

    init {
        vertices = mutableStateListOf()
        vertices.add(
            Vertex(
                id = 0,
                LatLng(47.3553999, 8.521167),
                8.0f,
                true,
                "Gray",
                sequence = 0
            )
        )
        vertices.add(
            Vertex(
                id = 1,
                LatLng(47.355246, 8.5221304),
                4.0f,
                true,
                "Gray",
                VertexRole.INSERTER,
                1
            )
        )
        vertices.add(
            Vertex(
                id = 2,
                LatLng(47.3552325, 8.522207),
                8.0f,
                true,
                "Gray",
                sequence = 2
            )
        )
        vertices.add(
            Vertex(
                id = 3,
                LatLng(47.3544909, 8.5218543),
                4.0f,
                true,
                "Gray",
                VertexRole.INSERTER,
                3
            )
        )
        vertices.add(
            Vertex(
                id = 4,
                LatLng(47.3546182, 8.521903),
                8.0f,
                true,
                "Gray",
                sequence = 4
            )
        )
        vertices.add(
            Vertex(
                id = 5,
                LatLng(47.3544909, 8.5218543),
                4.0f,
                true,
                "Gray",
                VertexRole.INSERTER,
                5
            )
        )
        vertices.add(
            Vertex(
                id = 6,
                LatLng(47.3546342, 8.5210538),
                8.0f,
                true,
                "Gray",
                sequence = 6
            )
        )
        vertices.add(
            Vertex(
                id = 7,
                LatLng(47.3545966, 8.5216044),
                4.0f,
                true,
                "Gray",
                VertexRole.INSERTER,
                7
            )
        )

        _verticesFlow.value = vertices

        // TODO this guy only collects once
        launch {
            _verticesFlow.collect {
                updateTransects()
            }
        }
        launch {
            _angleFlow.collect {
                updateTransects()
            }
        }
        launch {
            _transectSpacingFlow.collect {
                updateTransects()
            }
        }
    }

    fun handleVerticesTranslated(coords: List<LatLng>) {
        repeat(vertices.size) {
            if (vertices[it].role == VertexRole.DRAGGER) {
                vertices[it] = vertices[it].copy(location = coords[vertices[it].sequence / 2])
            }
        }
        updateTransects()
    }

    fun handleVertexChanged(id: Int, latLng: LatLng) {
        val changedVertex = vertices.first { it.id == id }
        val index = vertices.indexOfFirst { it.id == id }

        if (changedVertex.role == VertexRole.DRAGGER) {
            vertices[index] = changedVertex.copy(location = latLng)
        } else {
            val sequence = changedVertex.sequence
            vertices[index] = changedVertex.copy(
                location = latLng,
                role = VertexRole.DRAGGER,
                sequence = sequence + 1,
                radius = 8.0f
            )

            vertices.add(
                Vertex(
                    id = vertexId,
                    location = LatLng(),
                    radius = 4.0f,
                    draggable = true,
                    color = "Gray",
                    VertexRole.INSERTER,
                    sequence
                )
            )

            vertexId += 1

            vertices.add(
                Vertex(
                    id = vertexId,
                    location = LatLng(),
                    radius = 4.0f,
                    draggable = true,
                    color = "Gray",
                    VertexRole.INSERTER,
                    sequence + 2
                )
            )

            vertexId += 1

            for (i in 0..vertices.size - 3) {
                if (i != index && vertices[i].sequence > sequence) {
                    vertices[i] = vertices[i].copy(sequence = vertices[i].sequence + 2)
                }
            }

        }

        updateTransects()
    }

    fun deleteVertex(sequence: Int) {
        var index = vertices.indexOfFirst { it.sequence == sequence }

        if (index > -1) {
            val vertexToChange = vertices[index]

            val sequencePrev = (sequence - 1).wrapToListIndex(vertices.size)
            val sequenceNext = (sequence + 1).wrapToListIndex(vertices.size)

            vertices.removeIf { it.sequence == sequencePrev }
            vertices.removeIf { it.sequence == sequenceNext }

            index = vertices.indexOfFirst { it.sequence == sequence }

            vertices[index] = vertexToChange.copy(
                role = VertexRole.INSERTER,
                radius = 5.0f,
                sequence = sequencePrev
            )

            repeat(vertices.size) {
                if (vertices[it].sequence > sequence) {
                    vertices[it] = vertices[it].copy(
                        sequence = (vertices[it].sequence - 2).wrapToListIndex(vertices.size)
                    )
                }
            }

            updateTransects()
        }
    }

    private fun updateTransects() {
        if (vertices.isEmpty()) {
            return
        }

        val projection = LocalProjection(vertices.first { it.role == VertexRole.DRAGGER }.location)

        val draggerLocations = vertices
            .filter { it.role == VertexRole.DRAGGER }
            .map { projection.project(it.location) }

        val boundingRect = BoundingRectanglePolygon(draggerLocations)
        val boundRectCorners = boundingRect.getSquareEnlargedByFactor(2.0f)
        val transectSpacing = transectSpacingFlow.value

        val polygon = Polygon(vertices = vertices
            .sortedBy { it.sequence }
            .filter { it.role == VertexRole.DRAGGER }
            .map { projection.project(it.location) })

        val rotAngle = _angleFlow.value.toDouble()

        _transectFlow.value =
            createHorizontalLines(transectSpacing, boundRectCorners)
                .map { line -> rotateLineAroundCenter(line, rotAngle, boundingRect) }
                .mapNotNull { line ->
                    createTransect(
                        line,
                        polygon,
                        boundingRect,
                        rotAngle,
                        transectMargin,
                        projection,
                    )
                }
                .mapIndexed { index, transect -> alternateTransectDirection(index, transect) }
    }

    private fun createHorizontalLines(
        spacing: Float,
        boundRectCorners: BoundingRectangleCorners
    ): List<Line> {
        val yStart = boundRectCorners.topLeft.y
        val numTransects =
            ((boundRectCorners.bottomLeft.y - boundRectCorners.topLeft.y) / spacing).roundToInt()

        return MutableList(numTransects) { index ->
            Line(
                PointF(boundRectCorners.topLeft.x, yStart + (index * spacing)),
                PointF(boundRectCorners.topRight.x, yStart + (index * spacing))
            )
        }
    }

    private fun rotateLineAroundCenter(
        line: Line,
        rotAngle: Double,
        boundRect: BoundingRectanglePolygon
    ): Line {
        return line.rotateAroundCenter(boundRect.getCenterPoint(), rotAngle)
    }

    private fun createTransect(
        line: Line,
        polygon: Polygon,
        boundRect: BoundingRectanglePolygon,
        rotAngle: Double,
        transectMargin: Float,
        projection: LocalProjection,
    ): Transect? {
        val intersection = sortedIntersect(polygon, line, boundRect, rotAngle)

        // For a valid transect we need to intersect at least two sides of the polygon
        if (intersection.size <= 1) {
            return null
        }

        val candidateLine = Line(intersection.first().point, intersection.last().point)
        val trimmedLine = Line(
            candidateLine.start + candidateLine.getNormalizedDirection() * transectMargin,
            candidateLine.end - candidateLine.getNormalizedDirection() * transectMargin
        )

        // Skip transect if it's too short
        if (trimmedLine.getLength() < 2 * transectMargin) {
            return null
        }

        return Transect(
            trimmedLine,
            projection
        )
    }

    private fun sortedIntersect(
        polygon: Polygon,
        line: Line,
        boundingRect: BoundingRectanglePolygon,
        rotAngle: Double,
    ): List<LineIntersectionPoint> {
        return polygon.getIntersectionPoints(line)
            .map {
                // Rotate intersection points back to global frame for sorting
                LineIntersectionPoint(
                    it.point.rotateAroundCenter(boundingRect.getCenterPoint(), -rotAngle)
                )
            }
            .sortedBy { it.point.x }
            // Sort the intersection points, from left to right
            .map {
                // Rotate sorted points back to grid angle
                LineIntersectionPoint(
                    it.point.rotateAroundCenter(boundingRect.getCenterPoint(), rotAngle)
                )
            }
    }

    private fun alternateTransectDirection(index: Int, transect: Transect): Transect {
        // create lawnmower pattern by changing direction of every second transect
        return if (index % 2 == 0) {
            transect
        } else {
            Transect(
                Line(transect.photoLine.end, transect.photoLine.start),
                transect.projection,
            )
        }
    }

    fun setAngle(angle: Float) {
        _angleFlow.value = angle
    }

    fun setSpacing(spacing: Float) {
        _transectSpacingFlow.value = spacing
            .coerceAtLeast(minSpacing)
            .coerceAtMost(maxSpacing)
    }
}
