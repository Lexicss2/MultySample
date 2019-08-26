package com.amaiyorov.multysample

import android.Manifest
import android.app.Activity
import android.arch.persistence.room.Room
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
    private lateinit var temperatureTextView: TextView
    private lateinit var nameEditText: EditText

    private lateinit var database: AppDatabase

    @Inject
    lateinit var bleDeviceApi: BleDeviceApi

    // TODO: Unite to single status
    private var connectRequested: Boolean = false
    private var bluetoothOnRequested: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MultySampleApplication.appComponent.inject(this)
        connectButton = findViewById(R.id.btn_connect)
        connectButton.setOnClickListener {
            handleConnect()
        }

        bleEditText = findViewById(R.id.edt_ble_name)
        temperatureTextView = findViewById(R.id.txt_temperature)

        readButton = findViewById(R.id.btn_read)
        readButton.setOnClickListener {
            val itemDao = database.getItemDao()
            val items = itemDao.getItems()
            Log.d("qaz", "items read: ${items.size}")
            items.forEach {
                Log.v("qaz", "id = ${it.id}, name = ${it.name} / ${it.description}")
            }
        }

        insertButton = findViewById(R.id.btn_insert)
        insertButton.setOnClickListener {
            val itemDao = database.getItemDao()
//            val item = Item(0, "Alexandr", "------", 10)
//            val item2 = Item(0, "aaa", "dff", 2)
//            itemDao.insert(item)
//            itemDao.insert(item2)

            val name = nameEditText.text.toString()
            if (!name.isBlank()) {
                itemDao.insert(Item(0, name, "descr", 5))
                nameEditText.clear()
            }
        }
        nameEditText = findViewById(R.id.edt_name)

        database = Room.databaseBuilder(this, AppDatabase::class.java, "mydb")
            .allowMainThreadQueries()
            .build()
        // Use this example
        // https://medium.com/mindorks/room-kotlin-android-architecture-components-71cad5a1bb35
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
            bleDeviceApi.connect(bleEditText.text.toString(), notification)
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
            bleDeviceApi.connect(bleEditText.text.toString(), notification)
        }
    }


    private val notification = object : ActivityNotification {
        override fun onData(data: String) {
            runOnUiThread { temperatureTextView.text = data }
        }
    }

    interface ActivityNotification {
        fun onData(data: String)
    }

    fun EditText.clear() {
        this.setText("")
    }
}
