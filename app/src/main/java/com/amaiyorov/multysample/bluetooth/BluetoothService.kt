package com.amaiyorov.multysample.bluetooth

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class BluetoothService : Service() {
    private val devicesMap = mutableMapOf<String, NebulizerDevice>()
    private var _currentNebulizerDevice: NebulizerDevice? = null
    val nebulizerDevice: NebulizerDevice?
    get() = _currentNebulizerDevice
    private val binder = LocalBinder()
    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var isScanning: Boolean = false

    private var notification: NebulizerDevice.Notification? = null

    private val leScanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (result.device.name != null) {
                val device = result.device
                if (!devicesMap.contains(device.address)) {
                    val nebulizerDevice = NebulizerDevice(this@BluetoothService, device)
                    devicesMap[device.address] = nebulizerDevice
                    nebulizerDevice.connect(notification)
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        if (bluetoothManager == null) {
            throw IllegalArgumentException("Unable to initialize BluetoothManager")
        }
        bluetoothAdapter = bluetoothManager!!.adapter
        if (bluetoothAdapter == null) {
            throw IllegalArgumentException("Unable to initialize BluetoothManager")
        }

        bluetoothLeScanner = bluetoothAdapter!!.bluetoothLeScanner
        return binder
    }

    fun connect(address: String, notification: NebulizerDevice.Notification?): NebulizerDevice? {
        this.notification = notification
        if (devicesMap.containsKey(address)) {
            _currentNebulizerDevice = devicesMap[address]
            _currentNebulizerDevice?.connect(notification)
        } else {
            bluetoothAdapter?.getRemoteDevice(address)?.let { device ->
                val nebulizerDevice = NebulizerDevice(this, device)
                //nebulizerDevice.setNotification()
                devicesMap[address] = nebulizerDevice
                nebulizerDevice.connect(notification)
                _currentNebulizerDevice = nebulizerDevice
            }
        }

        return _currentNebulizerDevice
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    inner class LocalBinder : Binder() {
        private var _address: String? = null
        var address: String?
        get() = _address
        set(value) {
            _address = value
        }

        fun getService(): BluetoothService {
            return this@BluetoothService
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("qaz", "onServiceCreated")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("qaz", "onServiceDestroyed")
    }

    // alternative connect
    private fun startScan() {
        if (isScanning) return

        bluetoothLeScanner?.let { scanner ->
            Single.just(true)
                .map {
                    scanner.startScan(leScanCallback)
                }
                .subscribeOn(Schedulers.io())
                .subscribe()
            isScanning = true
        }
    }

    private fun stopScan() {
        if (!isScanning) return

        bluetoothLeScanner?.let { scanner ->
            Single.just(true)
                .map {
                    scanner.stopScan(leScanCallback)
                }
                .subscribeOn(Schedulers.io())
                .subscribe()
            isScanning = false
        }
    }
}