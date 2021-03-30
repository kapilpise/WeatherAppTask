package com.code.myweather.ui.home.citydetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.code.myweather.viewmodels.FiveDaysViewModel
import com.code.myweather.viewmodels.FiveDaysViewModelFactory
import com.code.myweather.util.hide
import com.code.myweather.util.show
import com.code.myweatherapp.R
import kotlinx.android.synthetic.main.fragment_five_days.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class FiveDaysFragment : Fragment(), KodeinAware {
    private val factory: FiveDaysViewModelFactory by instance()
    private lateinit var viewModel: FiveDaysViewModel

    var lat: Double = 0.0
    var lon: Double = 0.0
    var name: String? = null
    lateinit var fiveDaysAdapter: FiveDaysAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(FiveDaysViewModel::class.java)
        lat = arguments?.getDouble("lat")!!
        lon = arguments?.getDouble("lon")!!
        name = arguments?.getString("name")
        fiveDaysAdapter = FiveDaysAdapter(arrayListOf(), viewModel)
        return inflater.inflate(R.layout.fragment_five_days, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewFiveDays.layoutManager = LinearLayoutManager(context)
        recyclerViewFiveDays.adapter = fiveDaysAdapter
        viewModel.getForecastFromGps(lat.toString(), lon.toString())
        viewModel.forecastData.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                crdFiveDays.visibility = View.VISIBLE
                fiveDaysAdapter.updateHourlyList(list)
            }
        })

        viewModel.fiveDaysLoading.observe(viewLifecycleOwner, Observer { it ->
            if (it) {
                crdFiveDays.visibility = View.GONE
                pgFiveDaysLoading.show()
            } else {
                crdFiveDays.visibility = View.VISIBLE
                pgFiveDaysLoading.hide()
            }
        })
    }

    override val kodein by kodein()
}
