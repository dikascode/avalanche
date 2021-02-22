package com.dikascode.avalanche.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CartDao {

    @Query("SELECT * FROM RoomCartModel")
    fun getAll(): List<RoomCartModel>

    @Insert
    fun insertAll(vararg item: RoomCartModel)
}