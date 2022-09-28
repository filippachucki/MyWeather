package com.filippachucki.myweather

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SetupSystemBars() {
    val isLightTheme = MaterialTheme.colors.isLight
    val systemUiController = rememberSystemUiController()
    val navigationBarColor = MaterialTheme.colors.secondary
        .copy(alpha = NavigationBarOverlayAlpha)
        .compositeOver(MaterialTheme.colors.surface)

    SideEffect {
        systemUiController.setStatusBarColor(Color.Transparent, darkIcons = isLightTheme)
        systemUiController.setNavigationBarColor(navigationBarColor, darkIcons = isLightTheme)
    }
}

private const val NavigationBarOverlayAlpha = 0.12f
