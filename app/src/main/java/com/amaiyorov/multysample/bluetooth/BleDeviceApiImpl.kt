package com.amaiyorov.multysample.bluetooth

import android.content.Context
import android.util.Log
import javax.inject.Inject

class BleDeviceApiImpl : BleDeviceApi {

//    @Inject
//    lateinit var context: Context

    private lateinit var context: Context

    constructor(ctx: Context) {
        //MultySampleApplication.getAppComponent().inject(this)
        context = ctx

        if (context == null) {
            Log.e("qaz", "bad")
        } else {
            Log.i("qaz", "good")
        }
    }

    override fun connect() {
        Log.i("qaz", "connect called")
    }

    override fun disconnect() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}