package com.code.myweather.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

const val Current_City_Id = 0

@Entity
data class BookMarkedCities(
        val id: Int,
        val name: String,
        val state: String,
        val cordLat: Double,
        val cordLon: Double,
        val temp: Double?,
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = Current_City_Id
}