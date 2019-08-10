package com.amaiyorov.multysample.dagger

import com.amaiyorov.multysample.App
import com.amaiyorov.multysample.MainActivity
import com.amaiyorov.multysample.bluetooth.BleDeviceApiImpl
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ContextModule::class, BleDeviceModule::class])
interface AppComponent {
    fun inject(app: App)
    fun inject(activity: MainActivity)
    fun inject(api: BleDeviceApiImpl)
}