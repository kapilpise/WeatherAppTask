package com.code.myweather

import android.app.Application
import com.code.myweather.data.db.AppDB
import com.code.myweather.data.network.AppApiClient
import com.code.myweather.data.network.repositories.LocationRepository
import com.code.myweather.data.network.NetworkConnectionInterceptor
import com.code.myweather.data.preferance.PreferenceProvider
import com.code.myweather.viewmodels.FiveDaysViewModelFactory
import com.code.myweather.viewmodels.LocationViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class MVWeatherApp() : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@MVWeatherApp))
        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { AppApiClient(instance()) }
        bind() from singleton { AppDB(instance()) }
        bind() from singleton { PreferenceProvider(instance()) }
        //Repositories
        bind() from singleton { LocationRepository(instance(),instance()) }
        //View model factories
        bind() from provider { LocationViewModelFactory(instance(),instance()) }
        bind() from provider { FiveDaysViewModelFactory(instance(),instance()) }
    }
}