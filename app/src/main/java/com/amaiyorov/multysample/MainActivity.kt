package com.amaiyorov.multysample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.amaiyorov.multysample.bluetooth.BleDeviceApi
import com.amaiyorov.multysample.dagger.MultySampleApplication
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var connectButton: Button

    @Inject
    lateinit var bleDeviceApi: BleDeviceApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MultySampleApplication.appComponent.inject(this)
        connectButton = findViewById(R.id.btn_connect)

        bleDeviceApi.connect()
    }
}
