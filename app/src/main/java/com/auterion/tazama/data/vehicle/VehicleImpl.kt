package com.auterion.tazama.data.vehicle

import com.google.android.gms.maps.model.LatLng

class VehicleImpl : Vehicle, VehicleWriter {
    override val telemetry: Telemetry = TelemetryImpl()
    override val telemetryWriter = telemetry as TelemetryWriter

    override val camera: Camera = CameraImpl()
    override val cameraWriter = camera as CameraWriter

    override fun reset() {
        telemetryWriter.positionWriter.value = LatLng(0.0, 0.0)
        cameraWriter.videoStreamInfoWriter.value = VideoStreamInfo("")
    }
}