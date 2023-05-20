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
                        onVerticeAtIndexChanged = { index, vertice ->
                            survey.handleVerticeChanged(
                                index,
                                vertice
                            )
                        },
                        onDeleteVertice = {
                            survey.deleteVertice(it)
                        }
                    )
                }
            }
        }
    }
}

class Survey() {
    private var vertices = mutableStateListOf<Vertice>()
    private var _verticeFlow = MutableStateFlow(vertices)
    val verticeFlow = _verticeFlow.asStateFlow()

    var verticeId = 8

    init {
        vertices = mutableStateListOf<Vertice>()
        vertices.add(Vertice(id = 0, LatLng(4.8, 46.0), 8.0f, true, "Gray", sequence = 0))
        vertices.add(
            Vertice(
                id = 1,
                LatLng(4.8, 46.0),
                4.0f,
                true,
                "Gray",
                VerticeRole.INSERTER,
                1
            )
        )
        vertices.add(Vertice(id = 2, LatLng(4.8, 46.2), 8.0f, true, "Gray", sequence = 2))
        vertices.add(
            Vertice(
                id = 3,
                LatLng(4.8, 46.0),
                4.0f,
                true,
                "Gray",
                VerticeRole.INSERTER,
                3
            )
        )
        vertices.add(Vertice(id = 4, LatLng(4.6, 46.2), 8.0f, true, "Gray", sequence = 4))
        vertices.add(
            Vertice(
                id = 5,
                LatLng(4.8, 46.0),
                4.0f,
                true,
                "Gray",
                VerticeRole.INSERTER,
                5
            )
        )
        vertices.add(Vertice(id = 6, LatLng(4.6, 46.0), 8.0f, true, "Gray", sequence = 6))
        vertices.add(
            Vertice(
                id = 7,
                LatLng(4.8, 46.0),
                4.0f,
                true,
                "Gray",
                VerticeRole.INSERTER,
                7
            )
        )

        _verticeFlow.value = vertices
    }

    fun handleVerticesTranslated(coords: MutableList<LatLng>) {
        for (i in 0..vertices.size - 1) {
            if (vertices[i].role == VerticeRole.DRAGGER) {
                vertices[i] = vertices[i].copy(location = coords[vertices[i].sequence / 2])
            }
        }

        _verticeFlow.value = vertices
    }

    fun handleVerticeChanged(id: Int, latLng: LatLng) {
        val changedVertice = vertices.first { it.id == id }
        val index = vertices.indexOfFirst { it.id == id }

        if (changedVertice.role == VerticeRole.DRAGGER) {
            vertices[index] = changedVertice.copy(location = latLng)
        } else {
            val sequence = changedVertice.sequence
            vertices[index] = changedVertice.copy(
                location = latLng,
                role = VerticeRole.DRAGGER,
                sequence = sequence + 1,
                radius = 8.0f
            )

            vertices.add(
                Vertice(
                    id = verticeId,
                    location = LatLng(),
                    radius = 4.0f,
                    draggable = true,
                    color = "Gray",
                    VerticeRole.INSERTER,
                    sequence
                )
            )

            verticeId = verticeId + 1

            vertices.add(
                Vertice(
                    id = verticeId,
                    location = LatLng(),
                    radius = 4.0f,
                    draggable = true,
                    color = "Gray",
                    VerticeRole.INSERTER,
                    sequence + 2
                )
            )

            verticeId = verticeId + 1

            for (i in 0..vertices.size - 3) {
                if (i != index && vertices[i].sequence > sequence) {
                    vertices[i] = vertices[i].copy(sequence = vertices[i].sequence + 2)
                }
            }

        }

        println("\n\nlist modified or inserted")
        vertices.forEachIndexed { index, it ->
            println("index $index id ${it.id} sequence ${it.sequence} role ${it.role}")
        }
        println("\n\n")

        _verticeFlow.value = vertices

    }

    fun deleteVertice(sequence: Int) {

        val seq_saved = sequence

        var index = vertices.indexOfFirst { it.sequence == sequence }

        if (index > -1) {

            val vertexToChange = vertices[index]

            var sequencePrev = (sequence - 1).WrapToListIndex(vertices.size)
            val sequenceNext = (sequence + 1).WrapToListIndex(vertices.size)

            println("deleting sequence $sequence previous $sequencePrev next $sequenceNext")

            vertices.removeIf { it.sequence == sequencePrev }
            vertices.removeIf { it.sequence == sequenceNext }

            index = vertices.indexOfFirst { it.sequence == seq_saved }

            vertices[index] = vertexToChange.copy(
                role = VerticeRole.INSERTER,
                radius = 5.0f,
                sequence = sequencePrev
            )


            for (i in 0..vertices.size - 1) {
                if (vertices[i].sequence > sequence) {
                    vertices[i] = vertices[i].copy(
                        sequence = (vertices[i].sequence - 2).WrapToListIndex(vertices.size)
                    )
                }
            }
        }


        vertices.forEachIndexed { index, vertice ->
            println("index $index id ${vertice.id} sequence ${vertice.sequence} role ${vertice.role}")
        }


        _verticeFlow.value = vertices
    }
}
