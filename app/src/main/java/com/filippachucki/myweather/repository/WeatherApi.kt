package com.filippachucki.myweather.repository

import com.filippachucki.myweather.repository.model.Location
import com.filippachucki.myweather.repository.model.WeatherData
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/onecall")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String ="metric",
        @Query("exclude") exclute: String = "hourly,minutely",
        @Query("appid") appId: String = "e2ce613e3c3d6ac1684f8235bbafb351"
    ): WeatherData

    @GET("geo/1.0/direct")
    suspend fun getLocations(
        @Query("q") locationName: String,
        @Query("limit") limit: Int = 5,
        @Query("units") units: String ="metric",
        @Query("appid") appId: String = "e2ce613e3c3d6ac1684f8235bbafb351"
    ): List<Location>

    @GET("geo/1.0/reverse")
    suspend fun getLocationNames(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("limit") limit: Int = 5,
        @Query("appid") appId: String = "e2ce613e3c3d6ac1684f8235bbafb351"
    ): List<Location>
}