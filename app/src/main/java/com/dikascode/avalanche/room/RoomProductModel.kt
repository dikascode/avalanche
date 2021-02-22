package com.dikascode.avalanche.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomProductModel(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo val title: String,
    @ColumnInfo val price: Double,
    @ColumnInfo val desc: String
)