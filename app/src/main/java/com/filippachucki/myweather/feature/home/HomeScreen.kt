package com.filippachucki.myweather.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.filippachucki.myweather.R
import com.filippachucki.myweather.SetupSystemBars
import com.filippachucki.myweather.feature.destinations.AddLocationScreenDestination
import com.filippachucki.myweather.feature.destinations.DetailsScreenDestination
import com.filippachucki.myweather.feature.home.HomeViewState.*
import com.filippachucki.myweather.repository.model.DailyWeather
import com.filippachucki.myweather.repository.model.Location
import com.filippachucki.myweather.ui.theme.WeatherAppColor
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlin.math.roundToInt

@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<AddLocationScreenDestination, Location>,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    SetupSystemBars()
    val state = homeViewModel.uiState.collectAsState().value

    LaunchedEffect(Unit) { homeViewModel.init() }

    Box(contentAlignment = Alignment.Center) {
        state.errorMessage?.let {
            ErrorMessage(it) { homeViewModel.init() }
        }
        if (state.isLoading) {
            CircularProgressIndicator()
        }
    }
    Column(
        Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
            item {
                state.currentWeather?.let {
                    CurrentWeather(
                        locations = state.locations,
                        state = it,
                        onAddLocationClicked = { navigator.navigate(AddLocationScreenDestination) }
                    )
                }
            }
            items(state.dailyWeather) {
                DailyWeatherRow(it) { navigator.navigate(DetailsScreenDestination(it)) }
            }
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                homeViewModel.addLocation(result.value)
            }
        }
    }
}

@Composable
fun ErrorMessage(message: String, onRetryClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = message)
        Button(onClick = onRetryClick) { Text("Retry") }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DailyWeatherRow(
    dailyWeather: DailyWeather,
    onClick: (DailyWeather) -> Unit
) = with(dailyWeather) {
    val time = getDayOfWeek(dt.toLong() * 1000L)
    val icon = getWeatherIcon(weather.firstOrNull()?.icon)
    val maxTemp = temp.max.roundToInt().toString()
    val minTemp = temp.min.roundToInt().toString()

    Surface(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(2.dp, MaterialTheme.colors.secondary),
        onClick = { onClick(dailyWeather) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp)
        ) {
            val textPadding = with(LocalDensity.current) { 16.sp.toDp() + 12.dp }
            Text(modifier = Modifier.align(Alignment.CenterStart), text = time)
            Icon(
                modifier = Modifier.align(Alignment.Center),
                painter = painterResource(id = icon),
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = textPadding),
                text = "${maxTemp}°"
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .alpha(0.5f),
                text = "${minTemp}°"
            )
        }
    }
}


@Composable
private fun CurrentWeather(
    locations: List<LocationItem>,
    state: CurrentWeather,
    onAddLocationClicked: () -> Unit
) = with(state) {
    Column(modifier = Modifier.padding(24.dp)) {
        LocationHeader(locations, onAddLocationClicked)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "${currentTemperature}°", style = MaterialTheme.typography.h1)
                Text(
                    text = description,
                    modifier = Modifier
                        .offset(y = (-24).dp)
                        .background(
                            color = MaterialTheme.colors.secondary.copy(alpha = 0.25f),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }
            Icon(
                modifier = Modifier.size(120.dp),
                painter = painterResource(id = icon),
                contentDescription = null
            )
        }

        CurrentWeatherDetails(
            humidity = humidity,
            pressure = pressure,
            wind = wind
        )
    }
}

@Composable
private fun CurrentWeatherDetails(humidity: String, pressure: String, wind: String) = Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
) {
    WeatherDetailItem(icon = R.drawable.ic_humidity, text = "$humidity%")
    WeatherDetailItem(icon = R.drawable.ic_pressure, text = "${pressure}hPa")
    WeatherDetailItem(icon = R.drawable.ic_wind, text = "${wind}km/h")
}

@Composable
private fun WeatherDetailItem(icon: Int, text: String) = Row {
    Icon(painter = painterResource(id = icon), contentDescription = null)
    Spacer(modifier = Modifier.width(4.dp))
    Text(text)
}

@Composable
private fun LocationHeader(
    locations: List<LocationItem>,
    onAddLocationClicked: () -> Unit
) = Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    var isLocationSelectorExpanded by remember { mutableStateOf(false) }
    val name = locations.firstOrNull { it.isSelected }?.name ?: ""
    Box {
        Row(
            modifier = Modifier.clickable {
                isLocationSelectorExpanded = !isLocationSelectorExpanded
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = name, style = MaterialTheme.typography.h6)
            Icon(Icons.Filled.ArrowDropDown, null, modifier = Modifier.offset(y = (-3).dp))
        }
        DropdownMenu(
            expanded = isLocationSelectorExpanded,
            onDismissRequest = { isLocationSelectorExpanded = false },
        ) {
            locations.forEach {
                DropdownMenuItem(
                    onClick = {
                        it.onSelectedPressed(it)
                        isLocationSelectorExpanded = false
                    }
                ) {
                    Row(modifier = Modifier.width(200.dp), horizontalArrangement = SpaceBetween) {
                        Text(it.name)
                        if (locations.size > 1) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = null,
                                modifier = Modifier.clickable { it.onRemovePressed(it) }
                            )
                        }
                    }
                }
            }
        }
    }

    IconButton(onClick = { onAddLocationClicked() }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = null
        )
    }
}