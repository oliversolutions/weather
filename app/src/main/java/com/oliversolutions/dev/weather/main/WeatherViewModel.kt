package com.oliversolutions.dev.weather.main

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.oliversolutions.dev.weather.base.BaseViewModel
import com.oliversolutions.dev.weather.repository.WeatherRepository
import kotlinx.coroutines.launch
import com.oliversolutions.dev.weather.base.Result

class WeatherViewModel(application: Application, private val weatherRepository: WeatherRepository) : BaseViewModel(application) {
    var weather = MutableLiveData<Weather>()
    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeather(latitude: String, longitude: String, appId: String, units: String) {
        showLoading.value = true
        viewModelScope.launch {
            val result = weatherRepository.getWeather(latitude, longitude, appId, units)
            showLoading.value = false
            when (result) {
                is Result.Success<*> -> {
                    weather.value = result.data as Weather
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
            invalidateShowNoData()
        }
    }

    fun invalidateShowNoData() {
        showNoData.value = weather.value == null
    }

}