package com.auterion.tazama

import android.app.Application
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object VideoPlayerModule {

    @Provides
    @ViewModelScoped
    fun provideVideoPlayer(app: Application): ExoPlayer {
        val customLoadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(0, 0, 0, 0)
            .build()
        return ExoPlayer.Builder(app).setLoadControl(customLoadControl).build()
    }
}