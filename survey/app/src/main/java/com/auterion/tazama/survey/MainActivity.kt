package com.auterion.tazama.survey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.auterion.tazama.survey.ui.theme.TazamasurveyTheme
import org.ramani.compose.MapLibre
import kotlin.math.PI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TazamasurveyTheme {

                val surveyViewModel: SurveyViewModel = viewModel()
                val vertices = surveyViewModel.survey.verticeFlow.collectAsState()
                val transects = surveyViewModel.survey.transectFlow.collectAsState()
                val angle = surveyViewModel.survey.angleFlow.collectAsState()
                val spacing = surveyViewModel.survey.transectSpacingFlow.collectAsState()
                Box(modifier = Modifier.fillMaxSize()) {

                    Box(
                        modifier = Modifier
                            .zIndex(1.0f)
                            .width(300.dp)
                            .height(150.dp)
                            .background(color = Color.LightGray)
                            .align(Alignment.TopCenter)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp), verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            SliderItem(
                                "Grid Angle",
                                text = (angle.value * 180 / PI).toInt().toString(),
                                sliderValue = angle.value.toFloat(),
                                range = 0.0f..PI.toFloat(),
                                onValueChanged = { surveyViewModel.survey.setAngle(it) })

                            SliderItem(
                                label = "Spacing",
                                text = spacing.value.toInt().toString(),
                                sliderValue = spacing.value,
                                range = surveyViewModel.survey.minSpacing..surveyViewModel.survey.maxSpacing,
                                onValueChanged = { surveyViewModel.survey.setSpacing(it) }
                            )
                        }
                    }


                    MapLibre(
                        modifier = Modifier.fillMaxSize(),
                        apiKey = getString(R.string.maps_api_key),
                    ) {
                        SurveyPolygon(
                            vertices.value,
                            onVerticesTranslated = {
                                surveyViewModel.survey.handleVerticesTranslated(
                                    it
                                )
                            },
                            transects.value,
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

    @Composable
    private fun SliderItem(
        label: String,
        text: String,
        sliderValue: Float,
        range: ClosedFloatingPointRange<Float>,
        onValueChanged: (Float) -> Unit
    ) {
        Column() {
            Row() {
                Text(text = label)
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = text
                )
            }
            Slider(
                value = sliderValue,
                valueRange = range,
                onValueChange = onValueChanged
            )
        }
    }
}
