package com.auterion.tazama.presentation.components

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import com.auterion.tazama.data.vehicle.Degrees
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun VehicleMapMarker(
    context: Context,
    position: LatLng,
    title: String,
    rotation: Degrees = Degrees(0.0),
    @DrawableRes iconResourceId: Int
) {
    val iconSize = 150
    val icon = bitMapDescriptorFromVector(context, iconResourceId, iconSize)

    println("getting rotation " + rotation.value)

    Marker(
        state = MarkerState(position = position),
        title = title,
        icon = icon,
        rotation = rotation.value.toFloat()
    )
}

fun bitMapDescriptorFromVector(
    context: Context,
    vectorResId: Int,
    iconSize: Int
): BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, iconSize, iconSize)
    val bitmap = Bitmap.createBitmap(
        iconSize,
        iconSize,
        Bitmap.Config.RGBA_F16
    )

    val canvas = android.graphics.Canvas(bitmap)
    drawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
