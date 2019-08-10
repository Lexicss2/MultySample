package com.amaiyorov.multysample.bluetooth

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import javax.inject.Inject

class BleDeviceApiImpl(ctx: Context) : BleDeviceApi {

//    @Inject
//    lateinit var context: Context

    // TODO: Implement Scanning duscovering
    private val context: Context = ctx


    private var notification: NebulizerDevice.Notification? = null
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

    override fun connect() {
        Log.i("qaz", "connect called")
        if (bluetoothService == null) {
            // discover
        } else {
            bluetoothService!!.nebulizerDevice?.connect()
        }
    }

    override fun disconnect() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun connectToService(address: String) {

        serviceConnectionListener = object : OnServiceConnectionListener {
            override fun onServiceConnected(service: BluetoothService) {
                val bluetoothDevice = service.connect(address, notification)

                if (bluetoothDevice == null) {
                    // NebulizerStatus is BINDING
                    val gattServiceIntent = Intent(context, BluetoothService::class.java)
                    context.bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
                } else {
                    serviceConnectionListener?.onServiceConnected(service)
                }
            }
        }





//        onListener = { mNebulizerService ->
//            val mNebulizerDevice = mNebulizerService.connect(address, notification)
//            mNebulizerDevice
//        }
    }

    interface OnServiceConnectionListener {
        fun onServiceConnected(service: BluetoothService)
    }
}