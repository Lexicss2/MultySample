package com.amaiyorov.multysample.room.models

import android.arch.persistence.room.Entity

@Entity(tableName = "items")
data class Item(val id: Long, val name: String, val description: String?, val quantity: Long)