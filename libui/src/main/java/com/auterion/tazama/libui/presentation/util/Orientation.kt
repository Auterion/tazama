package com.auterion.tazama.libui.presentation.util

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalConfiguration

class Orientation {
    companion object {
        @Composable
        fun observeOrientation(): Int {
            var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }
            val configuration = LocalConfiguration.current

            LaunchedEffect(configuration) {
                snapshotFlow { configuration.orientation }
                    .collect { orientation = it }
            }
            return orientation
        }
    }
}