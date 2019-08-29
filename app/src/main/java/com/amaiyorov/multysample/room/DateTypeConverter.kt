package com.amaiyorov.multysample.room

import android.arch.persistence.room.TypeConverter
import java.util.*

class DateTypeConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? =
        if (value == null) null else Date(value)

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? =
            date?.time
}