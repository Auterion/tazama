package com.auterion.tazama.survey

import androidx.compose.runtime.mutableStateListOf
import com.auterion.tazama.survey.utils.geo.BoundingRectanglePolygon
import com.auterion.tazama.survey.utils.geo.Line
import com.auterion.tazama.survey.utils.geo.LineInterSectionPoint
import com.auterion.tazama.survey.utils.geo.LocalProjection
import com.auterion.tazama.survey.utils.geo.PointF
import com.auterion.tazama.survey.utils.geo.Polygon
import com.auterion.tazama.survey.utils.geo.rotate
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.PI
import kotlin.math.roundToInt

class Survey() {
    private var vertices = mutableStateListOf<Vertex>()
    private var _verticeFlow = MutableStateFlow(vertices)
    val verticeFlow = _verticeFlow.asStateFlow()

    //private val _transects = mutableStateListOf<LatLng>()
    private val _transectFlow = MutableStateFlow(emptyList<LatLng>())
    val transectFlow = _transectFlow.asStateFlow()

    private val _angleFlow = MutableStateFlow(0.0f)
    val angleFlow = _angleFlow.asStateFlow()

    private val _transectSpacingFlow = MutableStateFlow(50.0f)
    val transectSpacingFlow = _transectSpacingFlow.asStateFlow()
    val _minSpacing = 1.0f
    val minSpacing get() = _minSpacing
    val _maxSpacing = 500.0f
    val maxSpacing get() = _maxSpacing


    var verticeId = 8

    init {
//        runBlocking {
//            verticeFlow.collect {
//                updateTransects()
//            }
//        }
        vertices = mutableStateListOf<Vertex>()
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

        _verticeFlow.value = vertices
        updateTransects()
    }

    fun handleVerticesTranslated(coords: List<LatLng>) {
        repeat(vertices.size) {
            if (vertices[it].role == VertexRole.DRAGGER) {
                vertices[it] = vertices[it].copy(location = coords[vertices[it].sequence / 2])
            }
        }
        updateTransects()
        _verticeFlow.value = vertices
    }

    fun handleVerticeChanged(id: Int, latLng: LatLng) {

        val changedVertice = vertices.first { it.id == id }
        val index = vertices.indexOfFirst { it.id == id }

        if (changedVertice.role == VertexRole.DRAGGER) {
            vertices[index] = changedVertice.copy(location = latLng)
        } else {
            val sequence = changedVertice.sequence
            vertices[index] = changedVertice.copy(
                location = latLng,
                role = VertexRole.DRAGGER,
                sequence = sequence + 1,
                radius = 8.0f
            )

            vertices.add(
                Vertex(
                    id = verticeId,
                    location = LatLng(),
                    radius = 4.0f,
                    draggable = true,
                    color = "Gray",
                    VertexRole.INSERTER,
                    sequence
                )
            )

            verticeId += 1

            vertices.add(
                Vertex(
                    id = verticeId,
                    location = LatLng(),
                    radius = 4.0f,
                    draggable = true,
                    color = "Gray",
                    VertexRole.INSERTER,
                    sequence + 2
                )
            )

            verticeId += 1

            for (i in 0..vertices.size - 3) {
                if (i != index && vertices[i].sequence > sequence) {
                    vertices[i] = vertices[i].copy(sequence = vertices[i].sequence + 2)
                }
            }

        }

        var tmp = vertices
        updateTransects()
        _verticeFlow.value = tmp

    }

    fun deleteVertice(sequence: Int) {
        var index = vertices.indexOfFirst { it.sequence == sequence }

        if (index > -1) {
            val vertexToChange = vertices[index]

            val sequencePrev = (sequence - 1).WrapToListIndex(vertices.size)
            val sequenceNext = (sequence + 1).WrapToListIndex(vertices.size)

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
                        sequence = (vertices[it].sequence - 2).WrapToListIndex(vertices.size)
                    )
                }
            }
            updateTransects()
            _verticeFlow.value = vertices
        }
    }

    fun updateTransects() {
        if (vertices.isEmpty()) {
            return
        }

        val projection = LocalProjection(vertices.first { it.role == VertexRole.DRAGGER }.location)

        val boundRect =
            BoundingRectanglePolygon(
                vertices = vertices.filter { it.role == VertexRole.DRAGGER }
                    .map { projection.project(it.location) },
                vertices.first { it.role == VertexRole.DRAGGER }.location
            )

        val boundRectCorners = boundRect.getSquareCentered()

        val transectSpacing = transectSpacingFlow.value

        var yStart = boundRectCorners.topLeft.y
        val yEnd = boundRectCorners.bottomLeft.y

        val polygon = Polygon(vertices = vertices.sortedBy { it.sequence }
            .filter { it.role == VertexRole.DRAGGER }.map {
                projection.project(it.location)
            })

        var rotAngle = _angleFlow.value.toDouble()

        _transectFlow.value =
            MutableList(((yEnd - yStart) / transectSpacing).roundToInt()) { index ->
                // generates a list of indices according to number of horizontal lines we need
                index
            }.map {
                // creates horizontal line for each index
                Line(
                    PointF(boundRectCorners.topLeft.x, yStart + (it * transectSpacing).toFloat()),
                    PointF(boundRectCorners.topRight.x, yStart + (it * transectSpacing).toFloat())
                )
            }.mapIndexed { index, line ->

                val lineRot = line.rotateAroundCenter(boundRect.getCenterPoint(), rotAngle)
                val intersection = polygon.getIntersectionPoints(lineRot).map {
                    LineInterSectionPoint(it.point.rotate(-rotAngle))
                }.sortedBy { it.point.x }
                    .map {
                        LineInterSectionPoint(it.point.rotate(rotAngle))
                    }

                if (intersection.size <= 1) {
                    null
                } else {
                    Line(intersection.first().point, intersection.last().point)
                }


            }.filterNotNull().mapIndexed { index, line ->
                if (index % 2 == 0) {
                    line
                } else {
                    Line(line.end, line.start)
                }
            }.flatMap {
                listOf(it.start, it.end)
            }.map {
                projection.reproject(it)
            }
    }

    fun setAngle(angle: Float) {
        val angleConstrained = angle.coerceAtMost(PI.toFloat()).coerceAtLeast(0.0f)
        _angleFlow.value = angleConstrained
        // get rid of these
        updateTransects()
    }

    fun setSpacing(spacing: Float) {
        _transectSpacingFlow.value = spacing.coerceAtLeast(_minSpacing).coerceAtMost(_maxSpacing)
        // get rid of these
        updateTransects()
    }
}
