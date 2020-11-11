package com.decagon.avalanche.room

import android.content.Context
import androidx.room.Room

class RoomBuilder() {

    companion object {
        lateinit var instance: AppDatabase

        fun manageInstance(context: Context) : AppDatabase {
            instance = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "database_name"
            ).build()

            return instance
        }

    }
}