package com.amaiyorov.multysample.room.models

import android.arch.persistence.room.*
import com.amaiyorov.multysample.room.DateTypeConverter
import com.amaiyorov.multysample.room.ListConverter
import java.util.*

@Entity(tableName = "traineeRoutine")
data class Routine(
    @PrimaryKey(autoGenerate = true)
    val routineId: Int,
    @ColumnInfo(name = "due_day")
    val dueDay: Date,
    @TypeConverters(ListConverter::class)
    val exercises: List<Exercise>
    )