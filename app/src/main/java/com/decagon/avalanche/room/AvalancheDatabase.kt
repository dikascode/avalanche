package com.decagon.avalanche.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RoomProduct::class], version = 1)
abstract class AvalancheDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}