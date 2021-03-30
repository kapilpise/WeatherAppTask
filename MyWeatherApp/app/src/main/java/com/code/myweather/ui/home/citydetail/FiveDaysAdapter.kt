package com.code.myweather.ui.home.citydetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.code.myweather.data.network.responses.ForecastResponse
import com.code.myweather.util.dayConverter
import com.code.myweather.viewmodels.FiveDaysViewModel
import com.code.myweatherapp.R
import com.code.myweatherapp.databinding.ForecastWeatherHourlyItemBinding
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class FiveDaysAdapter(val hourlyList: ArrayList<ForecastResponse.Forecast>, val mViewModel: FiveDaysViewModel) :
        RecyclerView.Adapter<FiveDaysAdapter.HourlyViewHolder>() {

    class HourlyViewHolder(var view: ForecastWeatherHourlyItemBinding) :
            RecyclerView.ViewHolder(view.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ForecastWeatherHourlyItemBinding>(
                inflater,
                R.layout.forecast_weather_hourly_item,
                parent,
                false
        )
        return HourlyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return hourlyList.size
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        holder.view.forecast = hourlyList[position]

        holder.view.tvForecastTime.text = dayConverter((hourlyList[position].dt).toLong())
        holder.view.txtTemperature.text = hourlyList[position].main!!.temp.toInt().toString()
        holder.view.tvCityName.text = hourlyList[position].name
        holder.view.txtStat.text = hourlyList[position].weather!![0].description
        holder.view.txtWind.text = hourlyList[position].wind?.speed.toString()
        holder.view.txtHumid.text = hourlyList[position].main?.humidity.toString()
        if (mViewModel.getUnitsFlagStatus()!!) {
            holder.view.textViewUnit.text = "°F"
        } else {
            holder.view.textViewUnit.text = "°C"
        }
    }

    fun updateHourlyList(newHourlyList: List<ForecastResponse.Forecast>) {
        hourlyList.clear()
        val hMap = LinkedHashMap<String, ArrayList<ForecastResponse.Forecast>>()
        newHourlyList.sortedByDescending { it.dt }
        newHourlyList.forEach { item ->
            val date = dayConverter(item.dt.toLong())
            if (hMap[date] == null) {
                val newList = ArrayList<ForecastResponse.Forecast>()
                newList.add(item)
                hMap[date] = newList
            } else {
                val list = (hMap[date] as ArrayList<ForecastResponse.Forecast>)
                list.add(item)
                hMap[date] = list
            }
        }
        // sorting day wise data
        hMap.forEach { (key, value) ->
            hourlyList.add(value[0])
        }
        notifyDataSetChanged()
    }
}