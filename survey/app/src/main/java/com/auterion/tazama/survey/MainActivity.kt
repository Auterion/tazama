package com.auterion.tazama.survey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.auterion.tazama.survey.ui.theme.TazamasurveyTheme
import org.maplibre.compose.MapLibre
import org.maplibre.compose.SurveyPolygon

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TazamasurveyTheme {
                MapLibre(modifier = Modifier.fillMaxSize()) {
                    SurveyPolygon()
                }


            }
        }
    }
}
