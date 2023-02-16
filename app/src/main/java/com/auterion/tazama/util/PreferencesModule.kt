package com.auterion.tazama.util

import android.app.Application
import com.auterion.tazama.data.vehicle.Measure
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

// dagger-hilt does not seem to be able to return a flow directly as possibly it's an interface,
// therefore, wrap the flow into a holder
class FlowHolder(val flow: Flow<Measure.MeasurementSystem> = emptyFlow())

@InstallIn(ViewModelComponent::class)
@Module
object PreferencesModule {
    @ViewModelScoped
    @Provides
    fun provideMeasurementSystemFlow(application: Application): FlowHolder {
        val prefFlow = Preferences.getMeasureSystemFlow(application.applicationContext)
        val flow = prefFlow.map {
            when (it) {
                Preferences.MeasureSystem.METRIC -> Measure.MeasurementSystem.METRIC
                Preferences.MeasureSystem.IMPERIAL -> Measure.MeasurementSystem.IMPERIAL
            }
        }
        return FlowHolder(flow)
    }
}
