package com.filippachucki.myweather.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filippachucki.myweather.R
import com.filippachucki.myweather.extensions.launch
import com.filippachucki.myweather.feature.home.HomeViewState.*
import com.filippachucki.myweather.repository.SavedLocationsRepository
import com.filippachucki.myweather.repository.WeatherApi
import com.filippachucki.myweather.repository.model.DailyWeather
import com.filippachucki.myweather.repository.model.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

data class HomeViewState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val locations: List<LocationItem> = emptyList(),
    val currentWeather: CurrentWeather? = null,
    val dailyWeather: List<DailyWeather> = emptyList()
) {
    data class CurrentWeather(
        val description: String = "",
        val currentTemperature: String = "",
        val humidity: String = "",
        val wind: String = "",
        val pressure: String = "",
        val icon: Int
    )

    data class LocationItem(
        val name: String,
        val isSelected: Boolean,
        val onSelectedPressed: (LocationItem) -> Unit,
        val onRemovePressed: (LocationItem) -> Unit
    )
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherApi: WeatherApi,
    private val savedLocationsRepository: SavedLocationsRepository
) : ViewModel() {

    val uiState = MutableStateFlow(HomeViewState())

    fun init() {
        savedLocationsRepository.getLocations().firstOrNull()?.let { getLocationData(it) }
    }

    private fun getLocationData(location: Location) {
        updateState {
            copy(
                isLoading = true,
                errorMessage = null,
                currentWeather = null,
                dailyWeather = emptyList()
            )
        }
        viewModelScope.launch(
            onError = {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Could not load weather data"
                )
            },
            onSuccess = {
                updateState { copy(isLoading = true) }
                val data = weatherApi.getWeather(location.lat, location.lon)
                updateState {
                    copy(
                        isLoading = false,
                        locations = getLocations(location),
                        errorMessage = null,
                        currentWeather = CurrentWeather(
                            description = data.current.weather.firstOrNull()?.description ?: "",
                            currentTemperature = data.current.temp.roundToInt().toString(),
                            humidity = data.current.humidity.toString(),
                            wind = data.current.wind_speed.toString(),
                            pressure = data.current.pressure.toString(),
                            icon = getWeatherIcon(data.current.weather.firstOrNull()?.icon)
                        ),
                        dailyWeather = data.daily
                    )
                }
            }
        )
    }

    fun addLocation(location: Location) {
        savedLocationsRepository.addLocation(location)
        getLocationData(location)
    }

    private fun getLocations(selectedLocation: Location) =
        savedLocationsRepository.getLocations().map { location ->
            LocationItem(
                name = location.name,
                isSelected = selectedLocation == location,
                onSelectedPressed = {
                    getLocationData(location)
                },
                onRemovePressed = {
                    savedLocationsRepository.removeLocation(location)
                    savedLocationsRepository.getLocations().firstOrNull()?.let {
                        getLocationData(it)
                    }
                    updateState { copy(locations = locations.minus(it)) }
                }
            )
        }

    private fun updateState(block: HomeViewState.() -> HomeViewState) {
        uiState.value = block(uiState.value)
    }
}

fun getWeatherIcon(icon: String?): Int = when (icon) {
    "01d", "01n" -> R.drawable.ic_wi_day_sunny
    "02d", "02n" -> R.drawable.ic_wi_day_cloudy
    "03d", "03n" -> R.drawable.ic_wi_cloud
    "04d", "04n" -> R.drawable.ic_wi_cloudy
    "09d", "09n" -> R.drawable.ic_wi_rain
    "10d", "10n" -> R.drawable.ic_wi_day_rain
    "11d", "11n" -> R.drawable.ic_wi_thunderstorm
    "13d", "13n" -> R.drawable.ic_wi_snow
    "50d", "50n" -> R.drawable.ic_wi_fog
    else -> R.drawable.ic_wi_cloud
}

fun getDayOfWeek(long: Long): String {
    return try {
        val sdf = SimpleDateFormat("EEEE")
        val netDate = Date(long)
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}