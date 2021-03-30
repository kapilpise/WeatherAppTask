package com.code.myweather.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.code.myweather.data.preferance.PreferenceProvider
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*


fun ProgressBar.show() {
    visibility = View.VISIBLE
}

fun ProgressBar.hide() {
    visibility = View.GONE
}

@SuppressLint("SimpleDateFormat")
fun dayConverter(time: Long): String {
    var converter = SimpleDateFormat("EEE, d MMM yyyy")

    return converter.format(Date(time * 1000))
}

fun checkForUnitsFlag(pref: PreferenceProvider) = if (pref.getUnitPref()!!) {
    Constants.IMPERIAL
} else {
    Constants.METRIC
}