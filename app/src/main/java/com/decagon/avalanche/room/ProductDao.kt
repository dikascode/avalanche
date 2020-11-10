package com.decagon.avalanche.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductDao {

    @Query("SELECT * FROM RoomProducts")
    fun getAll(): List<RoomProducts>

    @Insert
    fun insertAll(vararg products: RoomProducts)
}