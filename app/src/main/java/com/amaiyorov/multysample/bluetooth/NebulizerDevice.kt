package com.amaiyorov.multysample.bluetooth

import android.bluetooth.*
import android.content.Context
import android.util.Log
import java.util.*

class NebulizerDevice(ctx: Context, device: BluetoothDevice) {

    companion object {
        internal val CMD_BATTERY_LEVEL = 0xBA.toByte()
        internal val CMD_NEBULIZATION_RATE = 0xA5.toByte()
        internal val DELAY_BATTERY = 1500L
        internal val DELAY_RATE = 3500L
        internal val NOTIFY_CHARACTERISTIC_UUID = "fff1"
        internal val CMD_POSITION = 2

        private val CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"
        private val UUID_CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG)
        private val SERVICE_CHARACTERISTIC = "0000fff0-0000-1000-8000-00805f9b34fb"
        private val UUID_SERVICE_CHARACTERISTIC = UUID.fromString(SERVICE_CHARACTERISTIC)
        private val NOTIFY_CHARACTERISTIC = "0000fff1-0000-1000-8000-00805f9b34fb"
        private val UUID_NOTIFY_CHARACTERISTIC = UUID.fromString(NOTIFY_CHARACTERISTIC)
        private val UUID_READ_CHARACTERISTIC = UUID.fromString(NOTIFY_CHARACTERISTIC)
        private val WRITE_CHARACTERISTIC = "0000fff2-0000-1000-8000-00805f9b34fb"
        private val UUID_WRITE_CHARACTERISTIC = UUID.fromString(WRITE_CHARACTERISTIC)
    }

    private var writeBluetoothGattCharacteristic: BluetoothGattCharacteristic? = null
    private var readBluetoothGattCharacteristic: BluetoothGattCharacteristic? = null
    private val context: Context = ctx
    private var address: String? = null
    private val device: BluetoothDevice = device
    private var connectState = ConnectionState.DISCONNECTED
    private var bluetoothGattWrapper: BluetoothGattWrapper? = null

    private var notification: NebulizerDevice.Notification? = null

    val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            Log.v("qaz", "onConnectionStateChanged: $newState")
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    setState(ConnectionState.CONNECTED)
                    val startDiscover = gatt?.discoverServices()
                    Log.d("qaz", "start discover Services: $startDiscover")
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    setState(ConnectionState.DISCONNECTED)

                    if (status != BluetoothGatt.GATT_SUCCESS) {
                        // notify device not found
                    }
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                setState(ConnectionState.ACTIVE)
                Log.i("qaz", "Services discovered")
                //initDevice()
                enableThermNotification(gatt!!)
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (characteristic != null) {
                    onDataRead(characteristic)
                }
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            if (characteristic != null) {
                onDataRead(characteristic)
            }
        }
    }

    fun connect(notification: NebulizerDevice.Notification?) {
        if (bluetoothGattWrapper != null && connectState != ConnectionState.DISCONNECTED) {
            return
        }

        this.notification = notification

        address = device.address
        bluetoothGattWrapper = BluetoothGattWrapper(device)
        bluetoothGattWrapper?.connectGatt(context, false, gattCallback)
        setState(ConnectionState.CONNECTING)
    }

    private fun requestBatteryLevel() {

    }

    private fun setState(state: ConnectionState) {
        connectState = state
    }

    private fun initDevice() {
        bluetoothGattWrapper!!.getService(UUID_SERVICE_CHARACTERISTIC)?.let { service ->
            readBluetoothGattCharacteristic = service.getCharacteristic(UUID_READ_CHARACTERISTIC)
            writeBluetoothGattCharacteristic = service.getCharacteristic(UUID_WRITE_CHARACTERISTIC)
            val notifyCharacteristic = service.getCharacteristic(UUID_NOTIFY_CHARACTERISTIC)
            setCharacteristicNotification(bluetoothGattWrapper!!, notifyCharacteristic, true)
        }

    }

    private fun enableThermNotification(gatt: BluetoothGatt) {
        gatt.services.forEach { service ->
            service.characteristics.forEach { characteristic ->
                val uuid = characteristic.uuid
                if (NOTIFY_CHARACTERISTIC == uuid.toString()) {
                    setThermCharacteristicsNotification(gatt, characteristic, true)
                }
            }
        }
    }

    private fun setThermCharacteristicsNotification(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, enable: Boolean) {
        gatt.setCharacteristicNotification(characteristic, enable)
        val uuid = UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG)
        val descriptor = characteristic.getDescriptor(uuid)
        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        val wrote = gatt.writeDescriptor(descriptor)
        Log.i("qaz", "Therm characteristics was wrote successfully: $wrote")
    }

    private fun setCharacteristicNotification(
        gattWrapper: BluetoothGattWrapper,
        notifyCharacteristic: BluetoothGattCharacteristic,
        enable: Boolean
    ) {
        gattWrapper.addCallToQueue(object : BluetoothGattWrapper.Callback {

            override fun doCommand(): Boolean {
                gattWrapper.setCharacteristicNotification(notifyCharacteristic, enable)
                val descriptor = notifyCharacteristic.getDescriptor(UUID_CLIENT_CHARACTERISTIC_CONFIG)
                descriptor.value =
                    if (enable) BluetoothGattDescriptor.ENABLE_INDICATION_VALUE else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                return gattWrapper.writeDescriptor(descriptor)
            }
        })
    }

    private fun onDataRead(characteristic: BluetoothGattCharacteristic) {
//        val fromUUid = characteristic.uuid.toString()
//        if (fromUUid.contains(NOTIFY_CHARACTERISTIC_UUID)) {
//            val buf = characteristic.value?.let { buf ->
//                val cmd = buf[0]
//
//                // TODO: Handle and Analyze buf array
//            }
//        }

        val data = characteristic.value
        val dataString = String(data)
        if (dataString.length > 7) {
            val temperature = dataString.substring(2, 7)
            Log.d("qaz", "temprature: $temperature")
            notification?.onThermData(temperature)
        }

        Log.i("qaz", "data extracted: $dataString (${dataString.length} bytes) " + data[3] + "," + data[4] + "," + data[5] + "," + data[6] + ", " + data[7])
    }

    private fun runCommand(byteCommand: Byte, callback: NebulizerCallback?) {
        bluetoothGattWrapper?.addCallToQueue(object : BluetoothGattWrapper.Callback {
            override fun doCommand(): Boolean {
                if (writeBluetoothGattCharacteristic == null) {
                    callback?.onRequestFailed()
                    return true
                }

                if (bluetoothGattWrapper == null) {
                    callback?.onRequestFailed()
                    return true
                }

                var status = false
                writeBluetoothGattCharacteristic?.let { characteristic ->
                    status = characteristic.setValue(byteCommand.toInt(), BluetoothGattCharacteristic.FORMAT_SINT8, 0)
                    characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                    status = bluetoothGattWrapper!!.writeCharacteristic(characteristic)

                    if (status) {
                        callback?.onRequestSuccess()
                    } else {
                        callback?.onRequestFailed()
                    }
                }

                return status
            }
        })
    }

    interface NebulizerCallback {
        fun onRequestSuccess()
        fun onRequestFailed()
    }

    interface Notification {
        fun onBatteryLevel(batteryLevel: Byte)

        fun onNebulizationRate(nebulizationRate: Byte)

//        fun onValve(valveState: Valve)

        fun onState(state: ConnectionState)

        fun onDeviceNotFound()

        fun onThermData(data: String)
    }
}