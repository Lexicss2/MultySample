package com.amaiyorov.multysample.room.dao

import android.arch.persistence.room.*
import com.amaiyorov.multysample.room.models.Exercise

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDayRoutine(exercise: Exercise)

    @Update
    fun updateDayRoutine(exercise: Exercise)

    @Delete
    fun deleteDayRoutine(exercise: Exercise)

    @Query("SELECT * FROM Exercise WHERE name == :exerciseName")
    fun getExerciseByName(exerciseName: String): List<Exercise>
}