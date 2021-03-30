package com.code.myweather.data.network.repositories

import com.code.myweather.data.db.AppDB
import com.code.myweather.data.db.entities.BookMarkedCities
import com.code.myweather.data.network.AppApiClient
import com.code.myweather.data.network.SafeApiNetworkCall
import com.code.myweather.data.network.responses.ForecastResponse
import com.code.myweather.data.preferance.PreferenceProvider
import com.code.myweather.util.Constants
import com.code.myweather.util.checkForUnitsFlag

class LocationRepository(
        private val apiClient: AppApiClient,
        private val db: AppDB
) : SafeApiNetworkCall() {

    suspend fun getForecastFromGps(latitude: String, longitude: String, pref: PreferenceProvider): ForecastResponse {
        return apiRequest {
            apiClient.getForecastByGPS(latitude, longitude, Constants.CNT, checkForUnitsFlag(pref))
        }
    }

    suspend fun saveCity(bookMarkedCities: BookMarkedCities?) = db.getBookMarCitiesDao()
            .upsertCity(bookMarkedCities!!)

    fun fetchAllCities() = db.getBookMarCitiesDao().getCity()

    suspend fun deleteCity(bookMarkedCities: BookMarkedCities?) {
        db.getBookMarCitiesDao().deleteCity(bookMarkedCities!!)
    }

    suspend fun deleteAllCity() {
        db.getBookMarCitiesDao().deleteAllCities()
    }

    suspend fun getCityWithName(name: String) =
            db.getBookMarCitiesDao().getCityName(name)
}