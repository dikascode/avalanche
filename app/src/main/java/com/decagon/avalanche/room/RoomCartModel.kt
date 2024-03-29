package com.decagon.avalanche.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomCartModel(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo val title: String,
    @ColumnInfo val price: Double,
    @ColumnInfo val quantity: Int
)