package com.auterion.tazama.survey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import com.auterion.tazama.survey.ui.theme.TazamasurveyTheme
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.maplibre.compose.MapLibre

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TazamasurveyTheme {
                val survey = Survey()

                val vertices = survey.verticeFlow.collectAsState()

                MapLibre(modifier = Modifier.fillMaxSize()) {
                    SurveyPolygon(
                        vertices.value.toMutableList(),
                        onVerticesTranslated = { survey.handleVerticesTranslated(it) },
                        onVertexWithIdChanged = { index, vertex ->
                            survey.handleVerticeChanged(
                                index,
                                vertex
                            )
                        },
                        onDeleteVertex = {
                            survey.deleteVertice(it)
                        }
                    )
                }
            }
        }
    }
}

class Survey() {
    private var vertices = mutableStateListOf<Vertex>()
    private var _verticeFlow = MutableStateFlow(vertices)
    val verticeFlow = _verticeFlow.asStateFlow()

    var verticeId = 8

    init {
        vertices = mutableStateListOf<Vertex>()
        vertices.add(Vertex(id = 0, LatLng(4.8, 46.0), 8.0f, true, "Gray", sequence = 0))
        vertices.add(
            Vertex(
                id = 1,
                LatLng(4.8, 46.0),
                4.0f,
                true,
                "Gray",
                VertexRole.INSERTER,
                1
            )
        )
        vertices.add(Vertex(id = 2, LatLng(4.8, 46.2), 8.0f, true, "Gray", sequence = 2))
        vertices.add(
            Vertex(
                id = 3,
                LatLng(4.8, 46.0),
                4.0f,
                true,
                "Gray",
                VertexRole.INSERTER,
                3
            )
        )
        vertices.add(Vertex(id = 4, LatLng(4.6, 46.2), 8.0f, true, "Gray", sequence = 4))
        vertices.add(
            Vertex(
                id = 5,
                LatLng(4.8, 46.0),
                4.0f,
                true,
                "Gray",
                VertexRole.INSERTER,
                5
            )
        )
        vertices.add(Vertex(id = 6, LatLng(4.6, 46.0), 8.0f, true, "Gray", sequence = 6))
        vertices.add(
            Vertex(
                id = 7,
                LatLng(4.8, 46.0),
                4.0f,
                true,
                "Gray",
                VertexRole.INSERTER,
                7
            )
        )

        _verticeFlow.value = vertices
    }

    fun handleVerticesTranslated(coords: MutableList<LatLng>) {
        println("vertices translated")
        repeat(vertices.size) {
            if (vertices[it].role == VertexRole.DRAGGER) {
                vertices[it] = vertices[it].copy(location = coords[vertices[it].sequence / 2])
            }
        }

        _verticeFlow.value = vertices
    }

    fun handleVerticeChanged(id: Int, latLng: LatLng) {
        println("vertice with id $id changed")

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
        _verticeFlow.value = vertices

    }

    fun deleteVertice(sequence: Int) {

        var index = vertices.indexOfFirst { it.sequence == sequence }

        if (index > -1) {

            val vertexToChange = vertices[index]

            var sequencePrev = (sequence - 1).WrapToListIndex(vertices.size)
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
            _verticeFlow.value = vertices
        }
    }
}
