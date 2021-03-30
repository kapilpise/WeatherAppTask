package com.code.myweather.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.code.myweather.data.network.repositories.LocationRepository
import com.code.myweather.data.preferance.PreferenceProvider

@Suppress("UNCHECKED_CAST")
class FiveDaysViewModelFactory(
    private val locationRepository: LocationRepository,
    private val pref: PreferenceProvider,

) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FiveDaysViewModel(locationRepository, pref) as T
    }
}