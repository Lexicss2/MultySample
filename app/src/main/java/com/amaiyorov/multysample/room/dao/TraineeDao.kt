package com.amaiyorov.multysample.room.dao

import android.arch.persistence.room.*
import com.amaiyorov.multysample.room.models.Trainee

@Dao
interface TraineeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrainee(trainee: Trainee)

    @Update
    fun updateTrainee(trainee: Trainee)

    @Delete
    fun deleteTrainee(trainee: Trainee)

    @Query("SELECT * FROM Trainee WHERE name = :nameToFind")
    fun getUserByName(nameToFind: String): List<Trainee>
}