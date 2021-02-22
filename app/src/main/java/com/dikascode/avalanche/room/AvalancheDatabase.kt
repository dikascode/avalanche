package com.dikascode.avalanche.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RoomProductModel::class, RoomCartModel::class], version = 1)
abstract class AvalancheDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
}