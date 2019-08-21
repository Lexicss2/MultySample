package com.amaiyorov.multysample.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.amaiyorov.multysample.MainActivity
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class BleDeviceApiImpl(ctx: Context) : BleDeviceApi {

//    @Inject
//    lateinit var context: Context

    // TODO: Implement Scanning duscovering
    private val context: Context = ctx


    private var notification: NebulizerDevice.Notification? = object : NebulizerDevice.Notification {

        override fun onBatteryLevel(batteryLevel: Byte) {

        }

        override fun onNebulizationRate(nebulizationRate: Byte) {

        }

        override fun onState(state: ConnectionState) {

        }

        override fun onDeviceNotFound() {

        }

        override fun onThermData(data: String) {
            activityNotification?.onData(data)
        }
    }
    private var bluetoothService: BluetoothService? = null
    private var serviceConnectionListener: OnServiceConnectionListener? = null
    private val serviceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            bluetoothService = null
            serviceConnectionListener = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            (service as BluetoothService.LocalBinder).getService()?.let { service ->
                serviceConnectionListener?.onServiceConnected(service)
                bluetoothService = service
            }

        }
    }

    private var bluetoothManager: BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter
    private var scanner: BluetoothLeScanner? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var isScanning: Boolean = false
    private var deviceName: String? = null
    private var activityNotification: MainActivity.ActivityNotification? = null

    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            // Implement
            deviceName?.let { name ->
                if (result.device.name != null) {
                    if (result.device.name == name) {
                        Log.i("qaz", "deviceName found: $name")
                        val address = result.device.address
                        connectToService(address)
                        stopScan()
                    }
                }
            }
        }
    }

    init {
        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter.isEnabled) {
            scanner = bluetoothAdapter.bluetoothLeScanner
        }
    }


    override fun connect(deviceName: String, notification: MainActivity.ActivityNotification?) {
        Log.i("qaz", "connect called")
        activityNotification = notification
        this.deviceName = deviceName
        if (scanner == null) {
            scanner = bluetoothAdapter.bluetoothLeScanner
        }

        if (bluetoothService == null) {
            // discover
            startScan()
        } else {
            bluetoothService!!.nebulizerDevice?.connect(this.notification)
        }
    }

    override fun disconnect() {

    }

    fun connectToService(address: String) {

        serviceConnectionListener = object : OnServiceConnectionListener {
            override fun onServiceConnected(service: BluetoothService) {
                Log.i("qaz", "onServiceConnected")
                val bluetoothDevice = service.connect(address, notification)
            }
        }

        if (bluetoothService == null) {
            // NebulizerStatus is BINDING
            val gattServiceIntent = Intent(context, BluetoothService::class.java)
            context.bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        } else {
            serviceConnectionListener?.onServiceConnected(bluetoothService!!)
        }
    }

    private fun startScan() {
        Single.just(true)
            .map {
                scanner!!.startScan(leScanCallback)
            }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun stopScan() {
        Single.just(true)
            .map {
                Log.d("qaz", "Stop scan scanner: $scanner" )
                scanner!!.stopScan(leScanCallback)
            }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    interface OnServiceConnectionListener {
        fun onServiceConnected(service: BluetoothService)
    }
}