package com.ekremkocak.alzheimer.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "locations",
    indices = [
        Index("timestamp", unique = true)
    ]
)
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)