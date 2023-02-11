package com.auterion.tazama.util

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

// dagger-hilt does not seem to be able to return a flow directly as possibly it's an interface,
// therefore, wrap the flow into a holder
class FlowHolder(val flow: Flow<Preferences.MeasureSystem> = emptyFlow())

@InstallIn(ViewModelComponent::class)
@Module
object PreferencesModule {
    @ViewModelScoped
    @Provides
    fun provideMeasurementSystemFlow(application: Application): FlowHolder {
        return FlowHolder(Preferences.getMeasureSystemFlow(application.applicationContext))
    }

}
