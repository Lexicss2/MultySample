package com.amaiyorov.multysample.dagger

import android.content.Context
import com.amaiyorov.multysample.bluetooth.BleDeviceApi
import com.amaiyorov.multysample.bluetooth.BleDeviceApiImpl
import dagger.Module
import dagger.Provides

@Module
class BleDeviceModule {

    @Provides
    fun bleDeviceModule(ctx: Context): BleDeviceApi =
        BleDeviceApiImpl(ctx)
}