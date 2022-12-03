package com.auterion.tazama.data

import com.example.tazama.data.VehicleRepository
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
    fun provideVehicleInterface() : VehicleInterface {
        return VehicleInterfaceDummyImpl()
    }

    @Singleton
    @Provides
    fun provideVehicleRepository(vehicleInterface : VehicleInterface) : VehicleRepository {
        return VehicleRepository(vehicleInterface)
    }
}
