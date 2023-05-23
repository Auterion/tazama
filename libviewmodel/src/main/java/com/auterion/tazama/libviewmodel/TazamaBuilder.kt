package com.auterion.tazama.libviewmodel

import android.app.Application
import com.auterion.tazama.libviewmodel.settings.Preferences
import com.auterion.tazama.libviewmodel.settings.SettingsViewModel
import com.auterion.tazama.libviewmodel.vehicle.VehicleRepository
import com.auterion.tazama.libviewmodel.vehicle.VehicleRepositoryImpl
import com.auterion.tazama.libviewmodel.vehicle.VehicleViewModel
import kotlinx.coroutines.flow.map

/**
 * This is a helper for creating ViewModels from libviewmodel.
 *
 * By default, it will create all the ViewModels with the library implementation. But it
 * allows extending the components as optional arguments, in which case those will be used.
 * For instance, the user could create their own VehicleRepository, pass it as an argument,
 * and have it used when vehicleViewModel is created.
 */
class TazamaBuilder(
    application: Application,
    settingsViewModel: SettingsViewModel? = null,
    vehicleRepository: VehicleRepository? = null,
    vehicleViewModel: VehicleViewModel? = null,
    preferences: Preferences? = null,
) {
    private val preferences = preferences ?: Preferences(application)
    val settingsViewModel = settingsViewModel ?: SettingsViewModel(application, this.preferences)

    private val vehicleType = this.settingsViewModel.vehicleType
    private val vehicleRepository = vehicleRepository ?: VehicleRepositoryImpl(vehicleType)
    private val measureSystem = this.settingsViewModel.measureSystem.map { it.toMeasurement() }

    val vehicleViewModel =
        vehicleViewModel ?: VehicleViewModel(this.vehicleRepository, measureSystem)
}
