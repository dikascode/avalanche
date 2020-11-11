package com.decagon.avalanche.room

import android.content.Context
import androidx.room.Room

class RoomBuilder() {

    companion object {
        @Volatile
        private var INSTANCE: AvalancheDatabase? = null

        fun getDatabase(context: Context) : AvalancheDatabase {
            val tempInstance = INSTANCE

            if(tempInstance != null) return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AvalancheDatabase::class.java,
                    "avalanche_db"
                ).build()

                INSTANCE = instance
                return instance

            }
        }

    }
}