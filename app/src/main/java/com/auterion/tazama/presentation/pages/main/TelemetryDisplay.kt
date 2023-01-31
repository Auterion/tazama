package com.auterion.tazama.presentation.pages.main

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
import com.auterion.tazama.R
import java.text.DecimalFormat

@Composable
fun TelemetryInfo(
    modifier: Modifier,
    distFromHome: Float,
    height: Float,
    speed: Float,
    heading: Float
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
                    0 ->
                        TelemetryElement(
                            image = painterResource(id = R.drawable.baseline_home_24),
                            value = DecimalFormat("#").format(distFromHome),
                            unit = "m"
                        )
                    1 ->
                        TelemetryElement(
                            image = painterResource(id = R.drawable.baseline_height_24),
                            value = DecimalFormat("###0.0").format(height),
                            unit = "m"
                        )
                    2 ->
                        TelemetryElement(
                            image = painterResource(id = R.drawable.baseline_speed_24),
                            value = DecimalFormat("###0.0").format(speed),
                            unit = "m/s"
                        )

                    3 ->
                        TelemetryElement(
                            image = painterResource(id = R.drawable.baseline_drone_map_symbol),
                            imageModifier = Modifier.rotate(heading - 90),
                            value = DecimalFormat("#").format(heading),
                            unit = "deg"
                        )
                }
            }
        }
    }

}

@Composable
fun TelemetryElement(
    image: Painter? = null,
    imageModifier: Modifier = Modifier,
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
                modifier = imageModifier
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
