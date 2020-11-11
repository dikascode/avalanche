package com.decagon.avalanche.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductDao {

    @Query("SELECT * FROM RoomProduct")
    fun getAll(): List<RoomProduct>

    @Query("SELECT * FROM RoomProduct WHERE title LIKE :term")
    fun homeSearch(term: String): List<RoomProduct>

    @Insert
    fun insertAll(vararg products: RoomProduct)
}