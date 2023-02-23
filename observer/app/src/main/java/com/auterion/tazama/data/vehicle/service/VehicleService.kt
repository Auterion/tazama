package com.auterion.tazama.data.vehicle.service

interface VehicleService {
    fun connect()
    suspend fun destroy()
}