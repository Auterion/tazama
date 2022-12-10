package com.auterion.tazama.presentation.pages.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.auterion.tazama.R
import com.auterion.tazama.data.VehicleViewModel
import com.auterion.tazama.presentation.components.VehicleMapMarker
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapView(
    vehicleViewModel: VehicleViewModel,
    modifier: Modifier
) {
    val vehiclePosition = vehicleViewModel.vehiclePosition.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(vehiclePosition.value, 10f)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        VehicleMapMarker(
            context = LocalContext.current,
            position = vehiclePosition.value,
            title = "Vehicle",
            iconResourceId = R.drawable.drone
        )
    }
}
