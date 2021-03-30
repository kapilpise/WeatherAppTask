package com.code.myweather.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.code.myweather.data.db.entities.BookMarkedCities

@Database(
    entities = [BookMarkedCities::class],
    version = 1
)
abstract class AppDB : RoomDatabase() {

    abstract fun getBookMarCitiesDao(): BookMarkCitiesDao

    companion object {
        @Volatile
        private var instance: AppDB? = null
        private var LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDb(context).also {
                instance = it
            }
        }

        private fun buildDb(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDB::class.java,
                "MyWeatherAppDB.db"
            ).build()
    }
}