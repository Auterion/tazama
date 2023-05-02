package com.auterion.tazama.survey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.auterion.tazama.survey.ui.components.CircleWithItem
import com.auterion.tazama.survey.ui.theme.TazamasurveyTheme
import com.mapbox.mapboxsdk.geometry.LatLng
import org.maplibre.compose.Circle
import org.maplibre.compose.Fill
import org.maplibre.compose.MapLibre
import org.maplibre.compose.PolyLine
import org.maplibre.compose.Symbol

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TazamasurveyTheme {
                MapLibre(modifier = Modifier.fillMaxSize()) {

                    val center1 = remember {
                        mutableStateOf(LatLng(4.7, 46.0))
                    }

                    val center2 = remember {
                        mutableStateOf(LatLng(4.7, 46.0))
                    }

                    val center3 = remember {
                        mutableStateOf(LatLng(4.7, 46.0))
                    }

                    val center4 = remember {
                        mutableStateOf(LatLng(4.7, 46.0))
                    }
                    Fill(points = mutableListOf(mutableListOf(center1.value, center2.value, center3.value, center4.value)), fillColor = "Green", opacity = 0.3f)
                    PolyLine(points = mutableListOf(center1.value, center2.value, center3.value, center4.value, center1.value), color = "Red", lineWidth = 2.0f)
                    CircleWithItem(center1.value, radius = 8.0f, draggable = true, color = "Transparent", borderWidth = 2.0f, borderColor = "Black", onCenterChanged = {center1.value = it})
                    CircleWithItem(center2.value, radius = 8.0f, draggable = true, color = "Transparent", borderWidth = 2.0f, borderColor = "Black", onCenterChanged = {center2.value = it})
                    CircleWithItem(center3.value, radius = 8.0f, draggable = true, color = "Transparent", borderWidth = 2.0f, borderColor = "Black", onCenterChanged = {center3.value = it})
                    CircleWithItem(center4.value, radius = 8.0f, draggable = true, color = "Transparent", borderWidth = 2.0f, borderColor = "Black", onCenterChanged = {center4.value = it})
                }


            }
        }
    }
}
