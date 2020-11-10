package com.decagon.avalanche.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RoomProducts::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}