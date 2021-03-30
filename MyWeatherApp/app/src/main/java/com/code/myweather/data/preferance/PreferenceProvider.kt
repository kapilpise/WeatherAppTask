package com.code.myweather.data.preferance

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

private const val KEY_SAVED_AT = "key_unit_flag"

class PreferenceProvider(context: Context) {
    private val appContext = context.applicationContext

    private val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun saveUnitPref(unitFlag: Boolean) {
        preferences.edit().putBoolean(KEY_SAVED_AT, unitFlag).apply()
    }

    fun getUnitPref(): Boolean? {
        return preferences.getBoolean(KEY_SAVED_AT, false)
    }
}