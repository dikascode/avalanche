package com.dikascode.avalanche.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductDao {

    @Query("SELECT * FROM RoomProductModel")
    fun getAll(): List<RoomProductModel>

    @Query("SELECT * FROM RoomProductModel WHERE title LIKE :term")
    fun homeSearch(term: String): List<RoomProductModel>

    @Insert
    fun insertAll(vararg products: RoomProductModel)
}