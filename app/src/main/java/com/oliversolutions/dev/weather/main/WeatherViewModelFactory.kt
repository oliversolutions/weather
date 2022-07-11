package com.oliversolutions.dev.weather.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oliversolutions.dev.weather.repository.WeatherRepository

class WeatherViewModelFactory(
    private val application: Application,
    private val weatherRepository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(application, weatherRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
