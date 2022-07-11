package com.oliversolutions.dev.weather.main

import com.squareup.moshi.Json


data class Weather(
    val weather: Array<MutableMap<String,String>>,
    @Json(name = "main")
    val temp: MutableMap<String,String>,
    val name: String
)