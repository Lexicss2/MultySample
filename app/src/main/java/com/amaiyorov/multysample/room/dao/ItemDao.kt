package com.amaiyorov.multysample.room.dao

import android.arch.persistence.room.*
import com.amaiyorov.multysample.room.models.Item

@Dao
interface ItemDao {
    @Insert
    fun insert(vararg items: Item)
    @Update
    fun update(vararg items: Item)
    @Delete
    fun delete(vararg items: Item)

    @Query("SELECT * FROM items")
    fun getItems(): List<Item>

    @Query("SELECT * FROM items WHERE id = :id")
    fun getItemById(id: Long?): Item
}