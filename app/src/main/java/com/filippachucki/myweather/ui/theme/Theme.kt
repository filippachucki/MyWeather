package com.filippachucki.myweather.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = WeatherAppColor.PrimaryDark,
    primaryVariant = WeatherAppColor.Primary,
    secondary = WeatherAppColor.SecondaryDark
)

private val LightColorPalette = lightColors(
    primary = WeatherAppColor.Primary,
    primaryVariant = WeatherAppColor.PrimaryDark,
    secondary = WeatherAppColor.Secondary
)

@Composable
fun MyWeatherTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}