package com.auterion.tazama.data

import com.auterion.tazama.presentation.pages.settings.SettingsViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object VehicleModule {
    @Singleton
    @Provides
    fun provideVehicleInterface() : Vehicle {
        return VehicleDummyImpl()
    }

    @Singleton
    @Provides
    fun provideVehicleRepository(vehicle : Vehicle,
                                 settingsViewModel: SettingsViewModel) : VehicleRepository {
        return VehicleRepository(vehicle, settingsViewModel)
    }

    @Singleton
    @Provides
    fun provideSettingsViewModel() : SettingsViewModel {
        return SettingsViewModel()
    }
}
