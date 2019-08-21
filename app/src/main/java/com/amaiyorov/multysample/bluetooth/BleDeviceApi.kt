package com.amaiyorov.multysample.bluetooth

interface BleDeviceApi {
    fun connect(deviceName: String)
    fun disconnect()
}