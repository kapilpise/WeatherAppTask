package com.code.myweather.viewmodels

import android.location.Address
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.code.myweather.data.db.entities.BookMarkedCities
import com.code.myweather.data.network.repositories.LocationRepository
import com.code.myweather.data.preferance.PreferenceProvider
import com.code.myweather.util.ApiException
import com.code.myweather.util.NoInternetException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationViewModel(
        private val locationRepository: LocationRepository,
        private val pref: PreferenceProvider
) : ViewModel() {

    val isLocationLoading = MutableLiveData<Boolean>()
    val isCityExists = MutableLiveData<Boolean>()
    val cityAdded = MutableLiveData<Boolean>()
    var cityDailyData = fetchAllCities()

    fun saveCity(address: Address) {
        viewModelScope.launch {
            Dispatchers.IO

            val city = address.locality ?: address.subAdminArea ?: address.thoroughfare
            ?: address.postalCode ?: "Unknown Locality"
            val bookMarkedCities = BookMarkedCities(
                    city.hashCode() ?: 0,
                    city,
                    address.adminArea!!,
                    address.latitude!!,
                    address.longitude!!,
                    0.0)
            val savedCities = locationRepository.getCityWithName(bookMarkedCities.name)
            if (savedCities == null) {
                locationRepository.saveCity(bookMarkedCities)
                fetchAllCities()
                cityAdded.value = true
            } else {
                isCityExists.value = true
            }
        }
    }

    fun fetchAllCities() = locationRepository.fetchAllCities()

    fun onDeleteClick(bookMarkedCities: BookMarkedCities) {
        viewModelScope.launch {
            locationRepository.deleteCity(bookMarkedCities)
        }
    }

    fun getUnitsFlagStatus(): Boolean? {
        return pref.getUnitPref()
    }

    fun changeUnitFlagStatus(check: Boolean) {
        pref.saveUnitPref(check)
    }

    fun deleteAllCityData() {
        viewModelScope.launch {
            locationRepository.deleteAllCity()
        }
    }
}