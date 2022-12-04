package com.auterion.tazama

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MainToolBar() {
    Row(modifier = Modifier.fillMaxWidth().background(color = Color.Gray)) {
        Icon(imageVector = Icons.Filled.Settings,
        contentDescription = "Settings",
        modifier = Modifier
            .height(50.dp)
            .width(50.dp))
    }
}