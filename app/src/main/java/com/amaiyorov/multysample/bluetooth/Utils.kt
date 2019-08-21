package com.amaiyorov.multysample.bluetooth

import android.bluetooth.BluetoothManager
import android.content.Context

const val REQUEST_BT_CONNECT = 1

fun isBluetoothEnable(ctx: Context): Boolean {
    val manager = ctx.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val adapter = manager.adapter ?: return false

    return adapter.isEnabled
}