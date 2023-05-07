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
import org.maplibre.compose.SurveyPolygon
import org.maplibre.compose.Vertice

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
                        onVerticesChanged = { survey.handleVerticeChanged(it) },
                        onVerticeAtIndexChanged = { index, latlng ->
                            survey.handleVerticeChanged(vertices.value.mapIndexed { mapIndex, vertice ->
                                if (mapIndex == index) {
                                    latlng
                                } else {
                                    vertice.location
                                }
                            }.toMutableList())

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

    fun handleVerticeChanged(vertices: MutableList<LatLng>) {
        val newList: MutableList<Vertice> = mutableListOf()

        vertices.forEachIndexed { index, latLng ->
            newList.add(this.vertices.value.elementAt(index).copy(location = latLng))
        }

        this.vertices.value = newList
    }

}
