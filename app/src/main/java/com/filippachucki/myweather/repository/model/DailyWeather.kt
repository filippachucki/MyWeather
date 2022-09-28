package com.filippachucki.myweather.repository.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DailyWeather(
    val dt: Int,
    val sunrise: Int,
    val sunset: Int,
    val moonrise: Int,
    val moonset: Int,
    val moon_phase: Double,
    val temp: Temp,
    val feels_like: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val wind_speed: Double,
    val wind_deg: Int,
    val wind_gust: Double,
    val weather: List<Weather>,
    val clouds: Int,
    val pop: Double,
    val rain: Double?,
    val uvi: Double
) : Parcelable