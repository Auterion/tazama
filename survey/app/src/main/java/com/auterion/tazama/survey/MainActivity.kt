/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package com.auterion.tazama.survey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.auterion.tazama.survey.ui.theme.TazamasurveyTheme
import com.mapbox.mapboxsdk.geometry.LatLng
import org.ramani.compose.CameraPosition
import org.ramani.compose.MapLibre

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TazamasurveyTheme {
                val surveyViewModel: SurveyViewModel = viewModel()
                val vertices = surveyViewModel.survey.verticesFlow.collectAsState()
                val transects = surveyViewModel.survey.transectFlow.collectAsState()
                val angle = surveyViewModel.survey.angleFlow.collectAsState()
                val spacing = surveyViewModel.survey.transectSpacingFlow.collectAsState()

                val cameraPosition = rememberSaveable {
                    mutableStateOf(
                        CameraPosition(
                            target = LatLng(47.3552, 8.5215),
                            zoom = 17.0
                        )
                    )
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .heightIn()
                            .zIndex(1.0f)
                            .width(300.dp)
                            .background(color = Color.LightGray)
                            .align(Alignment.TopCenter)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                        ) {
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
                        apiKey = getString(R.string.maplibre_api_key),
                        cameraPosition = cameraPosition.value
                    ) {
                        SurveyPolygon(
                            vertices.value,
                            transects.value,
                            angle.value,
                            onVerticesTranslated = {
                                surveyViewModel.survey.handleVerticesTranslated(it)
                            },
                            onVertexWithIdChanged = { index, vertex ->
                                surveyViewModel.survey.handleVertexChanged(index, vertex)
                            },
                            onDeleteVertex = { surveyViewModel.survey.deleteVertex(it) },
                            onGridAngleChanged = { surveyViewModel.survey.setAngle(it) }
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
        Column {
            Row {
                Text(text = label)
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = text)
            }
            Slider(
                value = sliderValue,
                valueRange = range,
                onValueChange = onValueChanged,
            )
        }
    }
}
