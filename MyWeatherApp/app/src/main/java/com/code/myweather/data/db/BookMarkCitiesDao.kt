package com.code.myweather.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.code.myweather.data.db.entities.BookMarkedCities

@Dao
interface BookMarkCitiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCity(cities: BookMarkedCities)

    @Query("SELECT * FROM BookMarkedCities")
    fun getCity(): LiveData<List<BookMarkedCities>>

    @Query("SELECT * FROM BookMarkedCities WHERE name = :cityName")
    suspend fun getCityName(cityName: String): BookMarkedCities?

    @Delete
    suspend fun deleteCity(cities: BookMarkedCities)

    @Query("DELETE FROM BookMarkedCities")
    suspend fun deleteAllCities()
}