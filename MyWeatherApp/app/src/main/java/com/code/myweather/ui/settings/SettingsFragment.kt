package com.code.myweather.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.code.myweather.viewmodels.LocationViewModel
import com.code.myweather.viewmodels.LocationViewModelFactory
import com.code.myweatherapp.R
import kotlinx.android.synthetic.main.fragment_settings.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class SettingsFragment : Fragment(), KodeinAware {
    private val factory: LocationViewModelFactory by instance()
    private lateinit var viewModel: LocationViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(LocationViewModel::class.java)
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        switchWidgetUnit.isChecked = viewModel.getUnitsFlagStatus()!!

        switchWidgetUnit.setOnCheckedChangeListener { compoundButton, check ->
            viewModel.changeUnitFlagStatus(check)
        }

        llCityData.setOnClickListener {
            val alertDialog: android.app.AlertDialog.Builder =
                    android.app.AlertDialog.Builder(activity)
            alertDialog.setTitle("Delete Location")
            alertDialog.setMessage("Do you want to Delete All locations?")
            alertDialog.setPositiveButton("YES") { _, _ ->
                try {
                    viewModel.deleteAllCityData()
                } catch (e: Exception) {
                    println(e)
                }
            }
            alertDialog.setNeutralButton("NO") { _, _ ->
            }
            alertDialog.show()

        }
    }

    override val kodein by kodein()
}