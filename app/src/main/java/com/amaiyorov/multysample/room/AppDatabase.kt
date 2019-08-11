package com.amaiyorov.multysample.room

import android.arch.persistence.room.RoomDatabase
import com.amaiyorov.multysample.room.dao.ItemDao

abstract class AppDatabase : RoomDatabase() {

    abstract fun getImemDao() : ItemDao
}