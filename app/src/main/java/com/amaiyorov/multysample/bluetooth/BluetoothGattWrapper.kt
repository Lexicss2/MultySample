package com.amaiyorov.multysample.bluetooth

import android.bluetooth.*
import android.content.Context
import android.util.Log
import java.util.*

class BluetoothGattWrapper(device: BluetoothDevice) {
    private val TAG = "BluetoothGattWrapper"
    internal val bluetoothDevice: BluetoothDevice = device
    private val mQueue = ArrayDeque<Callback>()
    private var mBluetoothGatt: BluetoothGatt? = null
    private var gattCallback: BluetoothGattCallback? = null
    private var isBusy: Boolean = false
    internal var mBluetoothGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            gattCallback!!.onConnectionStateChange(gatt, status, newState)
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            gattCallback!!.onServicesDiscovered(gatt, status)
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            gattCallback!!.onCharacteristicWrite(gatt, characteristic, status)
            isBusy = false
            next()
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            gattCallback!!.onCharacteristicRead(gatt, characteristic, status)
            isBusy = false
            next()
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)
            gattCallback!!.onCharacteristicChanged(gatt, characteristic)
            //  next();
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            super.onDescriptorWrite(gatt, descriptor, status)
            gattCallback!!.onDescriptorWrite(gatt, descriptor, status)
            isBusy = false
            next()
        }

        override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            super.onDescriptorRead(gatt, descriptor, status)
            gattCallback!!.onDescriptorRead(gatt, descriptor, status)
            isBusy = false
            next()
        }
    }///


//    fun BluetoothGattWrapper(bluetoothDevice: BluetoothDevice): ??? {
//        this.bluetoothDevice = bluetoothDevice
//    }

    fun connectGatt(
        context: Context, autoConnect: Boolean,
        callback: BluetoothGattCallback
    ) {
        this.gattCallback = callback
        mBluetoothGatt = bluetoothDevice.connectGatt(context, autoConnect, mBluetoothGattCallback)
    }

    fun updateBluetoothGatt(gatt: BluetoothGatt): BluetoothGattWrapper {
        mBluetoothGatt = gatt
        return this
    }

    fun getBluetoothGatt(): BluetoothGatt? {
        return mBluetoothGatt
    }

    fun discoverServices(): Boolean {
        return mBluetoothGatt!!.discoverServices()
    }

    fun setCharacteristicNotification(characteristic: BluetoothGattCharacteristic, b: Boolean): Boolean {
        val statusWr = mBluetoothGatt!!.setCharacteristicNotification(characteristic, b)
        Log.i(TAG, "writeDescriptor:$statusWr")
        return statusWr
    }

    operator fun next() {
        if (mQueue.size == 0) {
            return
        }

        val typeGatt = mQueue.poll()
        val status = typeGatt.doCommand()
        if (!status) {
            Log.e(TAG, "next() repeat!")
            addToQueue(typeGatt)
        }
    }

    internal fun addToQueue(callback: Callback) {
        mQueue.add(callback)
    }

    fun addCallToQueue(callback: Callback) {
        addToQueue(callback)
        if (!isBusy) {
            isBusy = true
            next()
        }
    }

    fun writeDescriptor(descriptor: BluetoothGattDescriptor): Boolean {
        val statusWr = mBluetoothGatt!!.writeDescriptor(descriptor)
        Log.i(TAG, "writeDescriptor:$statusWr")
        return statusWr
    }

    fun readDescriptor(descriptor: BluetoothGattDescriptor): Boolean {
        val statusWr = mBluetoothGatt!!.readDescriptor(descriptor)
        Log.i(TAG, "readDescriptor status:$statusWr")
        return statusWr
    }

    fun readCharacteristic(characteristic: BluetoothGattCharacteristic): Boolean {
        val statusWr = mBluetoothGatt!!.readCharacteristic(characteristic)

        Log.i(TAG, "readCharacteristic:" + statusWr + " " + characteristic.uuid)
        return statusWr
    }

    fun readRemoteRssi(): Boolean {
        return mBluetoothGatt!!.readRemoteRssi()
    }

    fun close() {
        if (mBluetoothGatt != null)
            mBluetoothGatt!!.close()
    }

    fun getService(batteryServiceUuid: UUID): BluetoothGattService? {
        if (mBluetoothGatt != null) {
            return mBluetoothGatt!!.getService(batteryServiceUuid)
        } else {
            Log.e(TAG, " getService(UUID batteryServiceUuid) = null")
            return null
        }
    }

    fun getServices(): List<BluetoothGattService>? {
        if (mBluetoothGatt != null) {
            return mBluetoothGatt!!.services
        } else {
            Log.e(TAG, " getService() = null")
            return null
        }
    }

    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic): Boolean {
        val statusWr = mBluetoothGatt!!.writeCharacteristic(characteristic)
        Log.i(TAG, "status writeCharacteristic:$statusWr")
        return statusWr
    }

    fun disconnect() {
        if (mBluetoothGatt != null)
            mBluetoothGatt!!.disconnect()
    }


    interface Callback {
        fun doCommand(): Boolean
    }
}