package com.auterion.tazama.presentation.pages.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.auterion.tazama.R
import com.auterion.tazama.data.vehicle.VehicleViewModel
import com.auterion.tazama.presentation.components.VehicleMapMarker
import com.auterion.tazama.presentation.pages.settings.SettingsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapView(
    vehicleViewModel: VehicleViewModel,
    modifier: Modifier
) {

    val settingsViewModel = hiltViewModel<SettingsViewModel>()
    val mapType = settingsViewModel.currentMapType.collectAsState()

    val vehiclePosition = vehicleViewModel.vehiclePosition.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(vehiclePosition.value, 10f)
    }

    val props =
        MapProperties(
            mapType = when (mapType.value) {
                "Satellite" -> MapType.SATELLITE
                "Normal" -> MapType.NORMAL
                "Hybrid" -> MapType.HYBRID
                else -> MapType.NORMAL
            }
        )

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = props
    ) {
        VehicleMapMarker(
            context = LocalContext.current,
            position = vehiclePosition.value,
            title = "Vehicle",
            iconResourceId = R.drawable.drone
        )
    }
}
