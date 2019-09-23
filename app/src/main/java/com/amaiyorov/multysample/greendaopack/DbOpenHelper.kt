package com.amaiyorov.multysample.greendaopack

import android.content.Context
import android.util.Log
import org.greenrobot.greendao.database.Database

class DbOpenHelper(context: Context, name: String) : DaoMaster.OpenHelper(context, name) {

    override fun onUpgrade(db: Database?, oldVersion: Int, newVersion: Int) {
        super.onUpgrade(db, oldVersion, newVersion)

        Log.d("qaz", "GreenDao, old db version: $oldVersion, newVersion: $newVersion")
    }
}