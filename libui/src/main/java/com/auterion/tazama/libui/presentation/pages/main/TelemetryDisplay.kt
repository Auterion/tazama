/*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this
* file, You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package com.auterion.tazama.libui.presentation.pages.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.auterion.tazama.libui.R
import java.text.DecimalFormat

data class TelemetryDisplayNumber(val value: Double? = null, val unit: String = "")

@Composable
fun TelemetryInfo(
    modifier: Modifier,
    distFromHome: TelemetryDisplayNumber,
    height: TelemetryDisplayNumber,
    speed: TelemetryDisplayNumber,
    heading: TelemetryDisplayNumber,
) {
    Surface(
        color = MaterialTheme.colors.onSecondary.copy(alpha = 0.5f),
        modifier = modifier
            .sizeIn(), shape = RoundedCornerShape(5.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .sizeIn(maxWidth = 250.dp)
                .padding(10.dp),
        ) {
            items(4) { index ->
                when (index) {
                    0 -> TelemetryElement(
                        image = painterResource(id = R.drawable.baseline_home_24),
                        value = distFromHome.value?.let { DecimalFormat("#").format(it) } ?: "N/A",
                        unit = distFromHome.unit,
                    )
                    1 -> TelemetryElement(
                        image = painterResource(id = R.drawable.baseline_height_24),
                        value = height.value?.let { DecimalFormat("###0.0").format(it) } ?: "N/A",
                        unit = height.unit,
                    )
                    2 -> TelemetryElement(
                        image = painterResource(id = R.drawable.baseline_speed_24),
                        value = speed.value?.let { DecimalFormat("###0.0").format(it) } ?: "N/A",
                        unit = speed.unit,
                    )
                    3 -> TelemetryElement(
                        image = painterResource(id = R.drawable.baseline_drone_map_symbol),
                        modifier = heading.value?.let { Modifier.rotate(it.toFloat() - 90) }
                            ?: Modifier,
                        value = heading.value?.let { DecimalFormat("#").format(it) } ?: "N/A",
                        unit = "deg",
                    )
                }
            }
        }
    }
}

@Composable
fun TelemetryElement(
    modifier: Modifier = Modifier,
    image: Painter? = null,
    textSymbol: String? = null,
    value: String,
    unit: String
) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        if (image != null) {
            Image(
                painter = image,
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = Color.White),
                modifier = modifier
            )
        } else if (textSymbol != null) {
            Text(
                text = textSymbol,
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.alignByBaseline()
            )
        }
        Text(
            text = value,
            color = Color.White,
            fontSize = 24.sp,
            modifier = Modifier.alignByBaseline()
        )
        Text(
            text = unit,
            color = Color.White, fontSize = 16.sp,
            modifier = Modifier.alignByBaseline()
        )
    }
}
