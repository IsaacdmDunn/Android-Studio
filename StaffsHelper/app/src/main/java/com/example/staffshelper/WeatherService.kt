package com.example.staffshelper

import retrofit2.Call
import retrofit2.http.GET

interface WeatherService {
    @GET("weather?lat=53.01&lon=-2.18&units=metric&APPID=35da027c11e34833da60b9504762eb29")

    fun getWeather(): Call<Weather>

}