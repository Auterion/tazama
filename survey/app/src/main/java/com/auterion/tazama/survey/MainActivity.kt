package com.auterion.tazama.survey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.auterion.tazama.survey.ui.theme.TazamasurveyTheme
import org.ramani.compose.MapLibre

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TazamasurveyTheme {

                val surveyViewModel: SurveyViewModel = viewModel()
                val vertices = surveyViewModel.survey.verticeFlow.collectAsState()

                MapLibre(
                    modifier = Modifier.fillMaxSize(),
                    apiKey = getString(R.string.maps_api_key),
                ) {
                    SurveyPolygon(
                        vertices.value.toMutableList(),
                        onVerticesTranslated = { surveyViewModel.survey.handleVerticesTranslated(it) },
                        onVertexWithIdChanged = { index, vertex ->
                            surveyViewModel.survey.handleVerticeChanged(
                                index,
                                vertex
                            )
                        },
                        onDeleteVertex = {
                            surveyViewModel.survey.deleteVertice(it)
                        },
                    )
                }
            }
        }
    }
}
