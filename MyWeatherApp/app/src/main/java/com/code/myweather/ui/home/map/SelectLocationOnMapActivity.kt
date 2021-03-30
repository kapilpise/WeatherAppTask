package com.code.myweather.ui.home.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.code.myweather.viewmodels.LocationViewModel
import com.code.myweather.viewmodels.LocationViewModelFactory
import com.code.myweatherapp.R
import com.code.myweatherapp.databinding.ActivitySelectLocationMapBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.IOException
import java.util.*


class SelectLocationOnMapActivity : AppCompatActivity(), OnMapReadyCallback, PermissionListener, KodeinAware {

    private val factory: LocationViewModelFactory by instance()
    private lateinit var viewModel: LocationViewModel

    override val kodein by kodein()

    companion object {
        const val REQUEST_CHECK_SETTINGS = 43
    }

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivitySelectLocationMapBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_select_location_map)
        viewModel = ViewModelProvider(this, factory).get(LocationViewModel::class.java)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        viewModel.isCityExists.observe(this, {
            if (it) {
                Toast.makeText(this, "This location already bookmarked",
                        Toast.LENGTH_LONG).show()
                viewModel.isCityExists.value = false
            }
        })
        viewModel.cityAdded.observe(this, {
            if (it) {
                Toast.makeText(this, "Location is bookmarked",
                        Toast.LENGTH_LONG).show()
                viewModel.cityAdded.value = false
                finish()
            }
        })
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap?) {
        googleMap = map ?: return
        if (isPermissionGiven()) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
            getCurrentLocation()
        } else {
            givePermission()
        }
    }

    var address: Address? = null
    private fun setDragOnMap() {
        val geocoder = Geocoder(this, Locale.getDefault())
        googleMap.setOnMarkerDragListener(object : OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {
                marker.hideInfoWindow()
            }

            override fun onMarkerDrag(marker: Marker) {}
            override fun onMarkerDragEnd(marker: Marker) {
                val latLng = marker.position
                try {
                    address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)[0]
                    val city = address!!.locality ?: address!!.subAdminArea
                    ?: address!!.thoroughfare
                    val state = address!!.getAddressLine(0)
                    marker.snippet = "$state , $city"
                    if (!marker.isInfoWindowShown) {
                        marker.showInfoWindow()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        })

        googleMap.setOnInfoWindowClickListener { map ->
            val latLng = map.position
            address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)[0]
            address?.let {
                val sss = address!!.locality?.toString()
                val alertDialog: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
                alertDialog.setTitle("Bookmark Location")
                alertDialog.setMessage("Do you want to Bookmark this location? ${sss?:""}")
                alertDialog.setPositiveButton("YES",
                        DialogInterface.OnClickListener { _, _ ->
                            address?.also {
                                // Save in DB
                                viewModel.saveCity(address!!)
                            }
                        })
                alertDialog.setNeutralButton("NO") { _, _ ->
                    finish()
                }
                alertDialog.setNegativeButton("Cancel") { _, _ ->
                }
                alertDialog.show()
            }
        }
    }

    private fun isPermissionGiven(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun givePermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(this)
                .check()
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        getCurrentLocation()
    }

    override fun onPermissionRationaleShouldBeShown(
            permission: PermissionRequest?,
            token: PermissionToken?
    ) {
        token!!.continuePermissionRequest()
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        Toast.makeText(this, "Permission required for showing location", Toast.LENGTH_LONG).show()
    }

    private fun getCurrentLocation() {

        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (10 * 1000).toLong()
        locationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        val result = LocationServices.getSettingsClient(this).checkLocationSettings(locationSettingsRequest)
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                if (response!!.locationSettingsStates.isLocationPresent) {
                    getLastLocation()
                }
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                    } catch (e: IntentSender.SendIntentException) {
                    } catch (e: ClassCastException) {
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful && task.result != null) {
                        val mLastLocation = task.result

                        var address = "No known address"

                        val gcd = Geocoder(this, Locale.getDefault())
                        val addresses: List<Address>
                        try {
                            addresses = gcd.getFromLocation(mLastLocation!!.latitude, mLastLocation.longitude, 1)
                            if (addresses.isNotEmpty()) {
                                address = addresses[0].getAddressLine(0)
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        val icon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.ic_pickup))
                        val marker = (MarkerOptions()
                                .position(LatLng(mLastLocation!!.latitude, mLastLocation.longitude))
                                .title("Location")
                                .draggable(true)
                                .snippet(address)
                                .icon(icon))
                        googleMap.addMarker(marker)

                        val cameraPosition = CameraPosition.Builder()
                                .target(LatLng(mLastLocation.latitude, mLastLocation.longitude))
                                .zoom(16f)
                                .build()
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                    } else {
                        Toast.makeText(this, "No current location found", Toast.LENGTH_LONG).show()
                    }
                    setDragOnMap()
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                if (resultCode == Activity.RESULT_OK) {
                    getCurrentLocation()
                }
                if (resultCode == 0) {
                    moveMaptoDefaultLocation()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun moveMaptoDefaultLocation() {
        val lat = "21.169158"
        val lon = "79.134422"
        var address = "No known address"

        val gcd = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>
        try {

            addresses = gcd.getFromLocation(lat.toDouble(), lon.toDouble(), 1)
            if (addresses.isNotEmpty()) {
                address = addresses[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val icon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.ic_pickup))
        val marker = (MarkerOptions()
                .position(LatLng(lat.toDouble(), lon.toDouble()))
                .title("Location")
                .draggable(true)
                .snippet(address)
                .icon(icon))
        googleMap.addMarker(marker)

        val cameraPosition = CameraPosition.Builder()
                .target(LatLng(lat.toDouble(), lon.toDouble()))
                .zoom(16f)
                .build()
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        setDragOnMap()
    }
}
