package com.oliversolutions.dev.weather.main

import android.os.Build
import androidx.annotation.RequiresApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(WeatherApi.API_URL)
    .build()

interface WeatherApiService {
    @RequiresApi(Build.VERSION_CODES.O)
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appid") appId: String,
        @Query("units") units: String) : Weather
}
object WeatherApi {
    const val API_URL = "https://api.openweathermap.org/"
    const val API_KEY = "204f8c2ef84dfb584520c7fa25e01216"
    val retrofitService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}
