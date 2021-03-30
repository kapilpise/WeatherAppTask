package com.code.myweather.ui.home.citydetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.code.myweather.viewmodels.FiveDaysViewModel
import com.code.myweather.viewmodels.FiveDaysViewModelFactory
import com.code.myweather.util.hide
import com.code.myweather.util.dayConverter
import com.code.myweather.util.show
import com.code.myweatherapp.R
import com.code.myweatherapp.databinding.FragmentOneDayDetailBinding
import kotlinx.android.synthetic.main.fragment_one_day_detail.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class CityDailyDetailFragment : Fragment(), KodeinAware {
    private val factory: FiveDaysViewModelFactory by instance()
    private lateinit var viewModel: FiveDaysViewModel

    private lateinit var dataBinding: FragmentOneDayDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    var lat: Double = 0.0
    var lon: Double = 0.0
    var name: String? = null
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        lat = arguments?.getDouble("lat")!!
        lon = arguments?.getDouble("lon")!!
        name = arguments?.getString("name")

        viewModel = ViewModelProvider(this, factory).get(FiveDaysViewModel::class.java)
        viewModel.forecastData.value?.get(0)?.name = name
        return inflater.inflate(R.layout.fragment_one_day_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getForecastFromGps(lat.toString(), lon.toString())
        viewModel.forecastData.observe(viewLifecycleOwner, Observer { it ->
            tvCityName.text = it[0].name ?: this.name
            txtDate.text = dayConverter(it[0].dt.toLong())
            txtTemperature.text = it[0].main?.temp?.toInt().toString()
            txtStat.text = it[0].weather!![0].description
            txtWind.text = it[0].wind?.speed.toString()
            txtHumid.text = it[0].main?.humidity.toString()
        })
        viewModel.fiveDaysLoading.observe(viewLifecycleOwner, Observer { it ->
            if (it) {
                crdDetailCity.visibility = View.GONE
                pgLoading.show()
            } else {
                crdDetailCity.visibility = View.VISIBLE
                pgLoading.hide()
            }
        })


        txtFiveDays.setOnClickListener {
            val bundle = bundleOf("lat" to lat,
                    "lon" to lon,
                    "name" to name)
            viewModel.onClickFiveDays(it, bundle)
        }
        if (viewModel.getUnitsFlagStatus()!!) {
            textViewUnit.text = "°F"
        } else {
            textViewUnit.text = "°C"
        }
    }

    override val kodein by kodein()
}
