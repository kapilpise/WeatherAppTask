package com.code.myweather.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.code.myweather.viewmodels.LocationViewModel
import com.code.myweather.viewmodels.LocationViewModelFactory
import com.code.myweather.ui.home.map.SelectLocationOnMapActivity
import com.code.myweatherapp.R
import kotlinx.android.synthetic.main.fragment_add_location.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class AddLocationFragment : Fragment(), KodeinAware {

    private val factory: LocationViewModelFactory by instance()
    private lateinit var viewModel: LocationViewModel

    private lateinit var cityDailyAdapter: CityDailyAdapter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(LocationViewModel::class.java)
        cityDailyAdapter = CityDailyAdapter(arrayListOf(), viewModel)
        return inflater.inflate(R.layout.fragment_add_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerViewCities.layoutManager = LinearLayoutManager(context)
        recyclerViewCities.adapter = cityDailyAdapter
//        viewModel.getWeatherDataWithGPS(latitude ?: "21.145800", longitude
//                ?: "79.088158", Constants.CNT, Constants.METRIC)
        viewModel.cityDailyData.observe(viewLifecycleOwner, Observer { cityDailyWeatherGps ->
            cityDailyWeatherGps.let {
                recyclerViewCities.visibility = View.VISIBLE
                cityDailyAdapter.updateCountryList(cityDailyWeatherGps)
                viewModel.isLocationLoading.value = false
            }
        })
        viewModel.isLocationLoading.observe(viewLifecycleOwner, Observer { loading ->
            loading?.let {
                if (it) {
                    cityDailyLoading.visibility = View.VISIBLE
                    recyclerViewCities.visibility = View.GONE
                } else {
                    cityDailyLoading.visibility = View.GONE
                }
            }
        })
        fab.setOnClickListener {
//            Navigation.findNavController(it).navigate(R.id.action_addLocFragment_to_selectLocationOnMap2)
            Intent(view.context, SelectLocationOnMapActivity::class.java).also {
                view.context.startActivity(it)
            }
        }
    }

    override val kodein by kodein()
}
