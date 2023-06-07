package com.auterion.tazama.libviewmodel

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.auterion.tazama.libviewmodel.settings.Preferences
import com.auterion.tazama.libviewmodel.settings.PreferencesImpl
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
    viewModelStoreOwner: ViewModelStoreOwner,
    settingsViewModel: SettingsViewModel? = null,
    vehicleRepository: VehicleRepository? = null,
    vehicleViewModel: VehicleViewModel? = null,
    preferences: Preferences? = null,
) {
    private val preferences = preferences ?: PreferencesImpl(application)

    val settingsViewModel = settingsViewModel ?: ViewModelProvider(
        viewModelStoreOwner,
        SettingsViewModel.factory(application, this.preferences)
    )[SettingsViewModel::class.java]

    private val vehicleType = this.settingsViewModel.vehicleType
    private val vehicleRepository = vehicleRepository ?: VehicleRepositoryImpl(vehicleType)
    private val measureSystem = this.settingsViewModel.measureSystem.map { it.toMeasurement() }

    val vehicleViewModel = vehicleViewModel ?: ViewModelProvider(
        viewModelStoreOwner,
        VehicleViewModel.factory(this.vehicleRepository, this.measureSystem)
    )[VehicleViewModel::class.java]
}
