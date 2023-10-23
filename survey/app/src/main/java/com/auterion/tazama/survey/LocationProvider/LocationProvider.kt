package com.auterion.tazama.survey.LocationProvider

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.mapbox.mapboxsdk.geometry.LatLng

class Location(val context: Context) : LocationListener {
    override fun onLocationChanged(location: Location) {
        lastLocation = LatLng(location.latitude, location.longitude)
    }

    val locationManager: LocationManager

    var lastLocation: LatLng? = null

    fun start() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                10000,
                10.0f,
                this as LocationListener
            )
        }
    }

    init {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

}