package com.amaiyorov.multysample.dagger

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(val app: MultySampleApplication) {

    @Provides
    @Singleton
    fun provideApp() = app
}