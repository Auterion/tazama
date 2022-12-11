package com.auterion.tazama.data.vehicle

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
    fun provideVehicleRepository(
        settingsViewModel: SettingsViewModel
    ): VehicleRepository {
        return VehicleRepository(settingsViewModel)
    }

    @Singleton
    @Provides
    fun provideSettingsViewModel(): SettingsViewModel {
        return SettingsViewModel()
    }
}
