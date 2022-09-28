package com.filippachucki.myweather.feature.welcome

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filippachucki.myweather.extensions.launch
import com.filippachucki.myweather.repository.LocationProvider
import com.filippachucki.myweather.repository.SavedLocationsRepository
import com.filippachucki.myweather.repository.WeatherApi
import com.filippachucki.myweather.repository.model.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

data class WelcomeScreenState(
    val isLoading: Boolean = false
)

@HiltViewModel
class WelcomeScreenViewModel @Inject constructor(
    private val locationProvider: LocationProvider,
    private val weatherApi: WeatherApi,
    private val savedLocationsRepository: SavedLocationsRepository
) : ViewModel() {

    val uiState = MutableStateFlow(WelcomeScreenState())
    val navigateToHomeScreen = MutableSharedFlow<Unit>()

    fun onPermissionGranted() {
        updateState { copy(isLoading = true) }
        locationProvider.getCurrentLocation(
            onSuccess = { lat, lon -> getLocationName(lat, lon) },
            onFailure = {
                addDefaultLocation()
                navigateToHomeScreen()
            }
        )
    }

    fun onPermissionDenied() {
        addDefaultLocation()
        navigateToHomeScreen()
    }

    private fun getLocationName(lat: Double, lon: Double) = viewModelScope.launch(
        onError = {
            Log.e("TEST", "Could not get location name", it)
            addDefaultLocation()
            navigateToHomeScreen()
        },
        onSuccess = {
            val location = weatherApi.getLocationNames(lat, lon).firstOrNull()
            val name = location?.name ?: "Your location"
            val country = location?.country ?: ""
            savedLocationsRepository.addLocation(Location(name, lat, lon, country))
            navigateToHomeScreen()
        }
    )

    private fun navigateToHomeScreen() = viewModelScope.launch {
        navigateToHomeScreen.emit(Unit)
        savedLocationsRepository.setFirstRun(false)
    }

    private fun addDefaultLocation() {
        savedLocationsRepository.addLocation(Location("London", 51.5072, 0.1276, "GB"))
    }

    private fun updateState(block: WelcomeScreenState.() -> WelcomeScreenState) {
        uiState.value = block(uiState.value)
    }
}

