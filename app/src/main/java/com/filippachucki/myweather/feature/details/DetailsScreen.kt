package com.filippachucki.myweather.feature.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filippachucki.myweather.R
import com.filippachucki.myweather.feature.home.getDayOfWeek
import com.filippachucki.myweather.feature.home.getWeatherIcon
import com.filippachucki.myweather.repository.model.DailyWeather
import com.google.accompanist.insets.navigationBarsPadding
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Destination
@Composable
fun DetailsScreen(
    navigator: DestinationsNavigator,
    dailyWeather: DailyWeather
) {
    Column(
        Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            IconButton(onClick = { navigator.navigateUp() }) {
                Icon(Icons.Default.ArrowBack, null)
            }
            Text(
                text = "Weather for " + getDayOfWeek(dailyWeather.dt.toLong() * 1000L),
                style = MaterialTheme.typography.h6
            )
        }
        Column(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    style = MaterialTheme.typography.h2,
                    text = dailyWeather.temp.max.roundToInt().toString() + "°"
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    modifier = Modifier
                        .alpha(0.5f),
                    style = MaterialTheme.typography.h2,
                    text = dailyWeather.temp.min.roundToInt().toString() + "°"
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    modifier = Modifier.size(150.dp),
                    painter = painterResource(id = getWeatherIcon(dailyWeather.weather.firstOrNull()?.icon)),
                    contentDescription = null
                )
            }
            DetailsRow(description = "Humidity", value = "${dailyWeather.humidity}%")
            Spacer(modifier = Modifier.height(8.dp))
            DetailsRow(description = "Pressure", value = "${dailyWeather.pressure}hPa")
            Spacer(modifier = Modifier.height(8.dp))
            DetailsRow(description = "Wind speed", value = "${dailyWeather.wind_speed}km/h")
            Spacer(modifier = Modifier.height(8.dp))
            DetailsRow(description = "Sunrise time", value = getTime(dailyWeather.sunrise * 1000L))
            Spacer(modifier = Modifier.height(8.dp))
            DetailsRow(description = "Sunset time", value = getTime(dailyWeather.sunset * 1000L))
            Spacer(modifier = Modifier.height(8.dp))
            DetailsRow(
                description = "Feels like",
                value = "${dailyWeather.feels_like.day.roundToInt()}°"
            )
            Spacer(modifier = Modifier.height(8.dp))
            DetailsRow(description = "Moonrise", value = getTime(dailyWeather.moonrise * 1000L))
            Spacer(modifier = Modifier.height(8.dp))
            DetailsRow(description = "Moonset", value = getTime(dailyWeather.moonset * 1000L))
        }
    }
}

@Composable
fun DetailsRow(
    description: String,
    value: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(2.dp, MaterialTheme.colors.secondary),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Text(text = description)
            Text(text = value)
        }
    }
}

fun getTime(long: Long): String {
    return try {
        val sdf = SimpleDateFormat("HH:mm")
        val netDate = Date(long)
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}