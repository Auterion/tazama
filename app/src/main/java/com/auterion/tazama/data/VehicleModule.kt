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
    fun provideVehicleInterface(): VehicleImpl {
        return VehicleImpl()
    }

    @Singleton
    @Provides
    fun provideVehicleDummy(): VehicleDummyImpl {
        return VehicleDummyImpl()
    }

    @Singleton
    @Provides
    fun provideVehicleRepository(
        vehicle: VehicleImpl,
        vehicleDummy: VehicleDummyImpl,
        settingsViewModel: SettingsViewModel
    ): VehicleRepository {
        return VehicleRepository(vehicle, vehicleDummy, settingsViewModel)
    }

    @Singleton
    @Provides
    fun provideSettingsViewModel(): SettingsViewModel {
        return SettingsViewModel()
    }
}
