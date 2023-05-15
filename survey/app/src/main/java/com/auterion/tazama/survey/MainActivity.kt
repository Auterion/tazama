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

    init {
        vertices = mutableStateListOf<Vertice>()
        vertices.add(Vertice(LatLng(4.8, 46.0), 8.0f, true, "Gray", sequence = 0))
        vertices.add(Vertice(LatLng(4.8, 46.0), 8.0f, true, "Gray", VerticeRole.INSERTER, 1))
        vertices.add(Vertice(LatLng(4.8, 46.2), 8.0f, true, "Gray", sequence = 2))
        vertices.add(Vertice(LatLng(4.8, 46.0), 8.0f, true, "Gray", VerticeRole.INSERTER, 3))
        vertices.add(Vertice(LatLng(4.6, 46.2), 8.0f, true, "Gray", sequence = 4))
        vertices.add(Vertice(LatLng(4.8, 46.0), 8.0f, true, "Gray", VerticeRole.INSERTER, 5))
        vertices.add(Vertice(LatLng(4.6, 46.0), 8.0f, true, "Gray", sequence = 6))
        vertices.add(Vertice(LatLng(4.8, 46.0), 8.0f, true, "Gray", VerticeRole.INSERTER, 7))

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

    fun handleVerticeChanged(index: Int, latLng: LatLng) {
        val changedVertice = vertices[index]

        if (changedVertice.role == VerticeRole.DRAGGER) {
            vertices[index] = changedVertice.copy(location = latLng)
        } else {
            val sequence = changedVertice.sequence
            vertices[index] = changedVertice.copy(
                location = latLng,
                role = VerticeRole.DRAGGER,
                sequence = sequence + 1
            )

            vertices.add(
                Vertice(
                    location = LatLng(),
                    radius = 8.0f,
                    draggable = true,
                    color = "Red",
                    VerticeRole.INSERTER,
                    sequence
                )
            )
            vertices.add(
                Vertice(
                    location = LatLng(),
                    radius = 8.0f,
                    draggable = true,
                    color = "Red",
                    VerticeRole.INSERTER,
                    sequence + 2
                )
            )

            for (i in 0..vertices.size - 3) {
                if (i != index && vertices[i].sequence > sequence) {
                    vertices[i] = vertices[i].copy(sequence = vertices[i].sequence + 2)
                }
            }

        }
        _verticeFlow.value = vertices

    }
}
