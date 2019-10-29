package com.amaiyorov.multysample.dagger

import android.app.Application
import android.util.Log
import com.amaiyorov.multysample.R
import com.amaiyorov.multysample.greendaopack.DaoMaster
import com.amaiyorov.multysample.greendaopack.DaoSession
import com.amaiyorov.multysample.greendaopack.DbOpenHelper

import com.amaiyorov.multysample.greendaopack.User
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import io.realm.Realm

class MultySampleApplication : Application() {

    companion object {
//        private val applicationComponent: AppComponent = DaggerAppComponent.builder()
//            .appModule(AppModule(this))
//            .contextModule(ContextModule(this))
//            .build()
//        fun getAppComponent() : AppComponent = applicationComponent

        private lateinit var _appComponent: AppComponent

        val appComponent: AppComponent
            get() = _appComponent

        private lateinit var daoSession: DaoSession
    }

    override fun onCreate() {
        super.onCreate()

        _appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .contextModule(ContextModule(this))
            .build()

        Realm.init(this)

        daoSession = DaoMaster(
                DbOpenHelper(this, "greendao_demo.db").writableDb).newSession()

        if (daoSession.userDao.loadAll().isEmpty()) {
            daoSession.userDao.insert(User(1L, "Aaaaa Bbbbb", "", ""))
        }

        val config = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))
            .twitterAuthConfig(TwitterAuthConfig(resources.getString(R.string.twitter_consumer_key), resources.getString(R.string.twitter_consume_secret)))
            .debug(true)
            .build()
        Twitter.initialize(config)
    }

    fun getDaoSession(): DaoSession = daoSession
}