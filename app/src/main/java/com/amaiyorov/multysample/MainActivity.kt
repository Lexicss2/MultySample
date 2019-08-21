package com.amaiyorov.multysample

import android.Manifest
import android.app.Activity
import android.arch.persistence.room.Room
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.amaiyorov.multysample.bluetooth.BleDeviceApi
import com.amaiyorov.multysample.bluetooth.REQUEST_BT_CONNECT
import com.amaiyorov.multysample.bluetooth.isBluetoothEnable
import com.amaiyorov.multysample.dagger.MultySampleApplication
import com.amaiyorov.multysample.room.AppDatabase
import com.amaiyorov.multysample.room.models.Item
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_ENABLE_BT = 1
        private const val PERMISSION_REQUEST_COARSE_LOCATION = 1
    }

    private lateinit var connectButton: Button
    private lateinit var readButton: Button
    private lateinit var insertButton: Button
    private lateinit var bleEditText: EditText

    private lateinit var database: AppDatabase

    @Inject
    lateinit var bleDeviceApi: BleDeviceApi

    // TODO: Unite to single status
    private var connectRequested: Boolean = false
    private var bluetoothOnRequested: Boolean = false

    // TODO: Implement permissions for BLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MultySampleApplication.appComponent.inject(this)
        connectButton = findViewById(R.id.btn_connect)
        connectButton.setOnClickListener {
            handleConnect()
        }

        bleEditText = findViewById(R.id.edt_ble_name)

        readButton = findViewById(R.id.btn_read)
        readButton.setOnClickListener {
            val itemDao = database.getImemDao()
            val items = itemDao.getItems()
            Log.d("qaz", "items read: ${items.size}")
        }

        insertButton = findViewById(R.id.btn_insert)
        insertButton.setOnClickListener {
            val itemDao = database.getImemDao()
            val item = Item(1, "Alex", "lalalala", 10)
            itemDao.insert(item)
        }

//        database = Room.databaseBuilder(this, AppDatabase::class.java, "mydb")
//            .allowMainThreadQueries()
//            .build()
    }

    override fun onStop() {
        super.onStop()
        connectRequested = false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_REQUEST_COARSE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (connectRequested) {
                        //bleDeviceApi.connect()
                        tryToConnect()
                    }
                }
            }
        }
    }

    private fun hasPermission(): Boolean =
            this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun handleConnect() {
        val permissions = listOf(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (hasPermission()) {
            //bleDeviceApi.connect()
            tryToConnect()
        } else {
            requestPermissions(permissions.toTypedArray(), PERMISSION_REQUEST_COARSE_LOCATION)
            connectRequested = true
        }
    }

    private fun tryToConnect() {
        if (isBluetoothEnable(this)) {
            bleDeviceApi.connect(bleEditText.text.toString())
        } else {
            requestBluetooth()
        }
    }

    private fun requestBluetooth() {
        if (bluetoothOnRequested) {
            return
        }

        val requestCode = REQUEST_BT_CONNECT
        val enableBleIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBleIntent, requestCode)
        bluetoothOnRequested = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        bluetoothOnRequested = false
        val result = resultCode == Activity.RESULT_OK && requestCode == REQUEST_BT_CONNECT

        if (result) {
            bleDeviceApi.connect(bleEditText.text.toString())
        }
    }
}
