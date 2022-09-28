package com.filippachucki.myweather.feature.addlocation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filippachucki.myweather.extensions.launch
import com.filippachucki.myweather.feature.addlocation.AddLocationState.*
import com.filippachucki.myweather.repository.WeatherApi
import com.filippachucki.myweather.repository.model.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

data class AddLocationState(
    val input: String = "",
    val isLoading: Boolean = false,
    val locations: List<Location> = emptyList()
)

@HiltViewModel
class AddLocationViewModel @Inject constructor(
    private val weatherApi: WeatherApi
) : ViewModel() {

    private var locationNameJob : Job? = null
    val uiState = MutableStateFlow(AddLocationState())

    fun onLocationNameChanged(text: String) {
        locationNameJob?.cancel()
        updateState { copy(input = text) }
        locationNameJob = viewModelScope.launch(
            onError = {
                Log.e("TEST", "error", it)
                updateState { copy(isLoading = false) }
            },
            onSuccess = {
                updateState { copy(isLoading = true, locations = emptyList()) }
                val locations = weatherApi.getLocations(text)
                updateState { copy(isLoading = false, locations = locations) }
            }
        )
    }

    private fun updateState(block: AddLocationState.() -> AddLocationState) {
        uiState.value = block(uiState.value)
    }
}