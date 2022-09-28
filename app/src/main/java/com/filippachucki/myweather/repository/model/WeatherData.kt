package com.filippachucki.myweather.repository.model

data class WeatherData (
	val lat : Double,
	val lon : Double,
	val timezone : String,
	val timezone_offset : Int,
	val current : CurrentWeather,
	val daily : List<DailyWeather>
)