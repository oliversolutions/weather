package com.oliversolutions.dev.weather.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.oliversolutions.dev.weather.main.Weather
import com.oliversolutions.dev.weather.main.WeatherApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.oliversolutions.dev.weather.base.Result

class WeatherRepository(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getWeather(latitude: String, longitude: String, appId: String, units: String): Result<Weather> =
        withContext(ioDispatcher) {
            return@withContext try {
                Result.Success(
                    WeatherApi.retrofitService.getWeather(
                        latitude,
                        longitude,
                        appId,
                        units
                    )
                )
            } catch (ex: Exception) {
                Result.Error(ex.localizedMessage)
            }
        }
}