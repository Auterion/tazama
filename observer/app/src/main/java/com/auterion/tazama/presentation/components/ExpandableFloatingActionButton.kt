package com.auterion.tazama.presentation.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.auterion.tazama.R
import com.auterion.tazama.navigation.SettingsDestination

enum class ExpandableFloatingActionButtonState {
    Expanded,
    Collapsed
}

data class ExpandedItemData(
    val iconId: Int,
    val label: String,
    val action: ExpandedItemAction
)

sealed class ExpandedItemAction {
    data class ActionNavigate(val route: String) : ExpandedItemAction()
    object ActionCenterOnVehicle : ExpandedItemAction()
    object ActionClearFlightPath : ExpandedItemAction()
}

@Composable
fun ExpandedItem(
    item: ExpandedItemData, onItemClicked: (ExpandedItemData) -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onItemClicked(item) }
            .size(40.dp)
            .clip(shape = CircleShape)
            .background(color = MaterialTheme.colors.secondary)
            .padding(10.dp)

    ) {
        Image(
            painter = painterResource(id = item.iconId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ExpandableFloatingActionButton(
    buttonState: ExpandableFloatingActionButtonState,
    onButtonStateChanged: (ExpandableFloatingActionButtonState) -> Unit,
    items: List<ExpandedItemData>,
    onItemClicked: (ExpandedItemData) -> Unit
) {
    val transition = updateTransition(targetState = buttonState, label = "transition")
    val rotate by transition.animateFloat(label = "rotate") {
        when (it) {
            ExpandableFloatingActionButtonState.Expanded -> 315.0f
            ExpandableFloatingActionButtonState.Collapsed -> 0f
        }
    }
    Column(horizontalAlignment = Alignment.End) {
        if (transition.currentState == ExpandableFloatingActionButtonState.Expanded) {
            items.forEach {
                Row {
                    Text(
                        text = it.label,
                        modifier = Modifier
                            .background(color = MaterialTheme.colors.secondary)
                            .align(alignment = Alignment.CenterVertically)
                            .padding(5.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    ExpandedItem(item = it, onItemClicked = {
                        onButtonStateChanged(ExpandableFloatingActionButtonState.Collapsed)
                        onItemClicked(it)
                    })
                }
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
        FloatingActionButton(
            onClick = {
                onButtonStateChanged(
                    when (transition.currentState) {
                        ExpandableFloatingActionButtonState.Expanded -> ExpandableFloatingActionButtonState.Collapsed
                        ExpandableFloatingActionButtonState.Collapsed -> ExpandableFloatingActionButtonState.Expanded
                    }
                )
            },
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.rotate(rotate)
            )
        }
    }
}

val expandedItemsData = listOf(
    ExpandedItemData(
        iconId = R.drawable.baseline_settings_24,
        label = "Settings",
        action = ExpandedItemAction.ActionNavigate(SettingsDestination.route)
    ),
    ExpandedItemData(
        iconId = R.drawable.drone,
        label = "Center Vehicle",
        action = ExpandedItemAction.ActionCenterOnVehicle
    ),
    ExpandedItemData(
        iconId = R.drawable.diagonal_arrow,
        label = "Clear Flight Path",
        action = ExpandedItemAction.ActionClearFlightPath
    )
)
