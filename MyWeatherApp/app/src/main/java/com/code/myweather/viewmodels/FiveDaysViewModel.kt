package com.code.myweather.viewmodels

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.code.myweather.data.network.repositories.LocationRepository
import com.code.myweather.data.network.responses.ForecastResponse
import com.code.myweather.data.preferance.PreferenceProvider
import com.code.myweatherapp.R
import kotlinx.coroutines.launch


class FiveDaysViewModel(
        private val locationRepository: LocationRepository,
        private val pref: PreferenceProvider
) : ViewModel() {


    val forecastData = MutableLiveData<List<ForecastResponse.Forecast>>()
    var forecastDataList: List<ForecastResponse.Forecast> = ArrayList()

    //    var detail = MutableLiveData<CityDailyResponse.Forecast>()
    val fiveDaysLoading = MutableLiveData<Boolean>()

    fun getForecastFromGps(latitude: String, longitude: String) {
        fiveDaysLoading.value = true
        viewModelScope.launch {
            val response = locationRepository.getForecastFromGps(latitude, longitude, pref)
            response.list?.let {
                var forecastRespons = response
                forecastDataList = forecastRespons.list!!

                forecastData.value = forecastDataList
                fiveDaysLoading.value = false
                Log.i("BİLGİ : ", "CALIŞTI")
            }
        }
    }

    fun onClickFiveDays(view: View, bundle: Bundle) {
        Navigation.findNavController(view).navigate(R.id.action_oneDayDetailFragment_to_fiveDaysFragment2,
                bundle)
    }

    fun getUnitsFlagStatus() : Boolean? {
        return pref.getUnitPref()
    }
}


