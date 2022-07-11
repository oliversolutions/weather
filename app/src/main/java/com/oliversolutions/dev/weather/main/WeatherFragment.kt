package com.oliversolutions.dev.weather.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.oliversolutions.dev.weather.R
import com.oliversolutions.dev.weather.base.BaseFragment
import com.oliversolutions.dev.weather.databinding.FragmentWeatherBinding
import com.oliversolutions.dev.weather.repository.WeatherRepository

class WeatherFragment : BaseFragment() {
    private lateinit var binding: FragmentWeatherBinding
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private var latitude: String? = null
    private var longitude: String? = null
    override lateinit var _viewModel: WeatherViewModel
    private var permissions: Array<String> = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    @RequiresApi(Build.VERSION_CODES.O)
    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value == true
            }
            if (granted) {
                getCurrentLocationWeather(false)
            } else {
                _viewModel.showToast.value = getString(R.string.give_location_permission)
                _viewModel.invalidateShowNoData()
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val viewModelFactory = WeatherViewModelFactory(requireActivity().application, WeatherRepository())
        _viewModel = ViewModelProvider(this, viewModelFactory).get(WeatherViewModel::class.java)
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = _viewModel
        observeWeather()
        binding.updateWeather.setOnClickListener{
            getCurrentLocationWeather(true)
        }
        getCurrentLocationWeather(true)
        return binding.root
    }

    private fun observeWeather() {
        _viewModel.weather.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.weather = it
            }
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    private fun getCurrentLocationWeather(checkPermission: Boolean) {
        if (!checkPermission || hasPermissions(activity as Context, permissions)) {
            if (isLocationEnabled()) {
                fusedLocationProvider = LocationServices.getFusedLocationProviderClient(activity as AppCompatActivity)
                fusedLocationProvider.lastLocation.addOnCompleteListener {
                    val location: Location? = it.result
                    if (location != null) {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        _viewModel.getWeather(latitude!!, longitude!!, WeatherApi.API_KEY, "metric")
                    } else {
                        _viewModel.showToast.value = getString(R.string.location_could_not_be_found)
                        _viewModel.invalidateShowNoData()
                    }
                }
            } else {
                _viewModel.showToast.value = getString(R.string.location_must_be_enabled)
                _viewModel.invalidateShowNoData()
            }
        } else {
            permReqLauncher.launch(permissions)
        }
    }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
}