package com.amaiyorov.multysample.room.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "items")
data class Item(@PrimaryKey(autoGenerate = true)
                var id: Long,

                @ColumnInfo(name = "title") var name: String,
                @ColumnInfo(name = "description") var description: String?,
                @ColumnInfo(name = "quantity") var quantity: Long)