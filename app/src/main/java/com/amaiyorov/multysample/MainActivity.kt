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
import com.amaiyorov.multysample.realm.Dog
import com.amaiyorov.multysample.realm.Person
import com.amaiyorov.multysample.room.AppDatabase
import com.amaiyorov.multysample.room.models.Gender
import com.amaiyorov.multysample.room.models.Item
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import io.realm.OrderedCollectionChangeSet
import io.realm.OrderedRealmCollectionChangeListener
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.util.*
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

    // Realm
    private lateinit var tryRealmButton: Button

    // GreenDao
    private lateinit var greenDaoTextView: TextView

    // FB
    private lateinit var fbLoginButton: LoginButton


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

        // Run it in separate thread
//        val genderDao = database.genderDao()
//        var gender1 = Gender(name = "Male")
//        var gender2 = Gender(name = "Female")
//
//        genderDao.apply {
//            this.insertGender(gender1)
//            this.insertGender(gender2)
//
//            val genders = database.genderDao().getGenders()
//            genders.forEach {
//                Log.d("qaz", "gender: ${it.name}")
//            }
//        }

        tryRealmButton = findViewById(R.id.btn_try_realm)
        tryRealmButton.setOnClickListener {
            val realm = Realm.getDefaultInstance()

            val puppies = realm.where<Dog>().findAll()

            puppies.addChangeListener(object :
                OrderedRealmCollectionChangeListener<RealmResults<Dog>> {
                override fun onChange(
                    t: RealmResults<Dog>,
                    changeSet: OrderedCollectionChangeSet
                ) {
                    val insertions = changeSet.insertions
                    Log.i("qaz", "onChange in puppies, insertions: ${insertions.size}")
                }
            })

            if (puppies.isEmpty()) {
                Log.d("qaz", "No puppies, create one")

                val dog = Dog().apply {
                    name = "Rex"
                    age = 1
                }

                realm.executeTransaction {
                    try {
                        realm.insert(dog)
                        Log.i("qaz", "inserted ok in thread ${Thread.currentThread().name}")
                    } catch (e: Exception) {
                        Log.e("qaz", "Exception $e")
                    }
                }
            } else {
                Log.d("qaz", "we have puppies: ${puppies.size}")
                puppies.forEach {
                    Log.d("qaz", "puppie: ${it.name} , ${it.age}")
                }
            }


            realm.executeTransaction {
                val someDog = Dog().apply {
                    age = 4
                    name = "Sally"
                }
                val managedDog = realm.copyToRealm(someDog)
                val randomUUID = Random().nextLong()//UUID.randomUUID().toString()
                Log.d("qaz", "random UUID: $randomUUID")
                val person = realm.createObject(Person::class.java, randomUUID)
                person.dog = managedDog

                try {
                    realm.insert(person)
                } catch ( e: Exception) {
                    Log.e("qaz", "Failed insert person: ${e.localizedMessage}")
                }

            }
        }

        // Put this in a different thread or use AsyncSession in greenDAO.
        // For Demo purpose, this query is made on main thread but it should in a different thread.
        val user = (application as MultySampleApplication).getDaoSession().userDao.load(1L)

        greenDaoTextView = findViewById(R.id.green_dao_text_view)

        if (user != null) {
            greenDaoTextView.text = user.name
        }


        // continue https://developers.facebook.com/docs/facebook-login/android
        val callbackManager = CallbackManager.Factory.create()
        fbLoginButton = findViewById(R.id.login_button)
        fbLoginButton.setReadPermissions("email")
        fbLoginButton.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onCancel() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onError(error: FacebookException?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

    }

    // https://realm.io/docs/java/latest/
    // here more about Realm

    // https://blog.mindorks.com/powerful-android-orm-greendao-3-tutorial
    // GreenDAO

    override fun onStop() {
        super.onStop()
        connectRequested = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
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
