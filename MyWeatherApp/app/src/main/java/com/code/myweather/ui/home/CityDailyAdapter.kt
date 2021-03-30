package com.code.myweather.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.code.myweather.data.db.entities.BookMarkedCities
import com.code.myweather.viewmodels.LocationViewModel
import com.code.myweatherapp.R
import com.code.myweatherapp.databinding.ForecastWeatherDailyItemBinding

class CityDailyAdapter(val cityDailyList: ArrayList<BookMarkedCities>, val mViewModel: LocationViewModel) :
        RecyclerView.Adapter<CityDailyAdapter.CityDailyViewHolder>() {


    class CityDailyViewHolder(var view: ForecastWeatherDailyItemBinding) :
            RecyclerView.ViewHolder(view.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityDailyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ForecastWeatherDailyItemBinding>(
                inflater, R.layout.forecast_weather_daily_item, parent, false
        )
        return CityDailyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cityDailyList.size
    }

    override fun onBindViewHolder(holder: CityDailyViewHolder, position: Int) {
        holder.view.cityDailyData = cityDailyList[position]
        holder.view.tvCityTemp.text = cityDailyList[position].temp!!.toInt().toString()
        holder.view.tvCountryCode.text = cityDailyList[position].id.toString()
        holder.itemView.setOnClickListener {
            val bundle = bundleOf("lat" to cityDailyList[position].cordLat,
                    "lon" to cityDailyList[position].cordLon,
                    "name" to cityDailyList[position].name)
            Navigation.findNavController(it).navigate(R.id.action_oneDayFragment_to_oneDayDetailFragment2, bundle)
        }
        holder.view.imgDelete.setOnClickListener {
            val alertDialog: android.app.AlertDialog.Builder =
                    android.app.AlertDialog.Builder(holder.view.imgDelete.context)
            alertDialog.setTitle("Delete Location")
            alertDialog.setMessage("Do you want to Delete this location? ${cityDailyList[position].name}")
            alertDialog.setPositiveButton("YES") { _, _ ->
                mViewModel.onDeleteClick(cityDailyList[position])
                notifyItemRemoved(position)
            }
            alertDialog.setNeutralButton("NO") { _, _ ->
            }
            alertDialog.show()
        }
    }

    fun updateCountryList(newCountryList: List<BookMarkedCities>) {
        cityDailyList.clear()
        cityDailyList.addAll(newCountryList)
        notifyDataSetChanged()
    }
}