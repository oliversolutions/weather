package com.oliversolutions.dev.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.oliversolutions.dev.weather.base.BaseFragment
import com.oliversolutions.dev.weather.databinding.FragmentSecondBinding
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : BaseFragment() {

    private var _binding: FragmentSecondBinding? = null
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private val REQUEST_LOCATION_PERMISSION = 1
    override val _viewModel by viewModels<WeatherViewModel>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val TAG: String = SecondFragment::class.java.simpleName
    var PERMISSIONS = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value == true
            }
            if (granted) {
                //displayCameraFragment()
                Log.i("weather", "permissions granted")
            } else {
                Log.i("weather", "permissions NOT granted")
                _viewModel.showToast.value = "Location need to be enabled in order to use this app."

            }
        }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (isLocationEnabled()) {
            fusedLocationProvider.lastLocation.addOnCompleteListener {
                val location: Location? = it.result
                if (location != null) {
                    Log.i("weather", location.latitude.toString())
                    Log.i("weather", location.longitude.toString())
                }
            }
        } else {
            _viewModel.showToast.value = "Location need to be enabled in order to use this app."
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (hasPermissions(activity as Context, PERMISSIONS)) {
            fusedLocationProvider =
                LocationServices.getFusedLocationProviderClient(activity as AppCompatActivity)
            getCurrentLocation()
            lifecycleScope.launch {
                val weather = WeatherApi.retrofitService.getWeather(
                    "39.6950088",
                    "3.3506343",
                    "204f8c2ef84dfb584520c7fa25e01216",
                    "metric"
                )
                Log.i("output", weather.weather[0]["main"]!!)
            }
        } else {
            permReqLauncher.launch(
                PERMISSIONS
            )
        }


        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}