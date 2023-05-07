package com.auterion.tazama.survey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.auterion.tazama.survey.ui.theme.TazamasurveyTheme
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import org.maplibre.compose.MapLibre

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TazamasurveyTheme {
                val survey = Survey()

                val vertices = survey.vertices.collectAsState()

                MapLibre(modifier = Modifier.fillMaxSize()) {
                    SurveyPolygon(
                        vertices.value.toMutableList(),
                        onVerticesChanged = { survey.handleVerticesChanged(it) },
                        onVerticeAtIndexChanged = { index, vertice ->
                            survey.handleVerticeChanged(
                                index,
                                vertice
                            )
                        }, onVerticeInsert = { index, location ->
                            survey.insertVertice(index, location)
                        })
                }
            }
        }
    }
}

class Survey() {
    val vertices: MutableStateFlow<List<Vertice>> = MutableStateFlow(listOf())

    init {
        val newList = mutableListOf<Vertice>()
        newList.add(Vertice(LatLng(4.8, 46.0), 8.0f, true, "Gray"))
        newList.add(Vertice(LatLng(4.8, 46.2), 8.0f, true, "Gray"))
        newList.add(Vertice(LatLng(4.6, 46.2), 8.0f, true, "Gray"))
        newList.add(Vertice(LatLng(4.6, 46.0), 8.0f, true, "Gray"))

        vertices.value = newList
    }

    fun handleVerticesChanged(vertices: MutableList<Vertice>) {
        val newList: MutableList<Vertice> = vertices
        this.vertices.value = newList
    }

    fun handleVerticeChanged(index: Int, vertice: Vertice) {

        val newList: MutableList<Vertice> = vertices.value.toMutableList()
        newList[index] = vertice
        this.vertices.value = newList
    }

    fun insertVertice(index: Int, location: LatLng) {

        val newList: MutableList<Vertice> = vertices.value.toMutableList()
        newList.add(index + 1, vertices.value.first().copy(location = location))
        vertices.value = newList
    }

}
