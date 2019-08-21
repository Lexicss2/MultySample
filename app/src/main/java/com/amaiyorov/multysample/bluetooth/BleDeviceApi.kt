package com.amaiyorov.multysample.bluetooth

import com.amaiyorov.multysample.MainActivity

interface BleDeviceApi {
    fun connect(deviceName: String, listener: MainActivity.ActivityNotification?)
    fun disconnect()
}