package com.amaiyorov.multysample.dagger

import android.app.Application

class MultySampleApplication : Application() {

    companion object {
//        private val applicationComponent: AppComponent = DaggerAppComponent.builder()
//            .appModule(AppModule(this))
//            .contextModule(ContextModule(this))
//            .build()
//        fun getAppComponent() : AppComponent = applicationComponent

        private lateinit var _appComponent: AppComponent

        val appComponent: AppComponent
            get() = _appComponent
    }

    override fun onCreate() {
        super.onCreate()

        _appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .contextModule(ContextModule(this))
            .build()
    }
}