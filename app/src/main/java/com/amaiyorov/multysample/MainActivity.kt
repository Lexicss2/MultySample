package com.amaiyorov.multysample

import android.arch.persistence.room.Room
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.amaiyorov.multysample.bluetooth.BleDeviceApi
import com.amaiyorov.multysample.dagger.MultySampleApplication
import com.amaiyorov.multysample.room.AppDatabase
import com.amaiyorov.multysample.room.models.Item
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var connectButton: Button
    private lateinit var readButton: Button
    private lateinit var insertButton: Button

    private lateinit var database: AppDatabase

    @Inject
    lateinit var bleDeviceApi: BleDeviceApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MultySampleApplication.appComponent.inject(this)
        connectButton = findViewById(R.id.btn_connect)
        connectButton.setOnClickListener { bleDeviceApi.connect() }

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

        database = Room.databaseBuilder(this, AppDatabase::class.java, "mydb")
            .allowMainThreadQueries()
            .build()
    }
}
