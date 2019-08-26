package com.amaiyorov.multysample.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.amaiyorov.multysample.room.dao.ItemDao
import com.amaiyorov.multysample.room.models.Item

@Database(entities = [Item::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getItemDao() : ItemDao
}