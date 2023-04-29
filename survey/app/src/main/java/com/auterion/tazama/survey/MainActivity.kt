package com.auterion.tazama.survey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.auterion.tazama.survey.ui.theme.TazamasurveyTheme
import com.mapbox.mapboxsdk.geometry.LatLng
import org.maplibre.compose.Circle
import org.maplibre.compose.MapLibre

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TazamasurveyTheme {
                MapLibre(modifier = Modifier.fillMaxSize()) {
                    Circle(
                        center = LatLng(0.0, 0.0),
                        draggable = true,
                        color = "Red",
                    )
                }
            }
        }
    }
}
