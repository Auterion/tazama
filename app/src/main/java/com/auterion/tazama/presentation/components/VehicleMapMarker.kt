package com.auterion.tazama.map

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import com.auterion.tazama.R
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
    @DrawableRes iconResourceId: Int
) {
    val iconSize = 100
    val icon = bitMapDescriptorFromVector(context, iconResourceId, iconSize)

    Marker(
        state = MarkerState(position = position),
        title = title,
        icon = icon
    )
}

fun bitMapDescriptorFromVector(
    context: Context,
    vectorResId: Int,
    iconSize: Int
) : BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, iconSize, iconSize)
    val bm = Bitmap.createBitmap(
        iconSize,
        iconSize,
        Bitmap.Config.ARGB_8888
    )

    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}

