package com.filippachucki.myweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.filippachucki.myweather.Splash.initializeSplashScreen
import com.filippachucki.myweather.feature.NavGraphs
import com.filippachucki.myweather.feature.destinations.HomeScreenDestination
import com.filippachucki.myweather.feature.destinations.WelcomeScreenDestination
import com.filippachucki.myweather.feature.welcome.WelcomeScreen
import com.filippachucki.myweather.repository.SavedLocationsRepository
import com.filippachucki.myweather.ui.theme.MyWeatherTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.animatedComposable
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var savedLocationsRepository: SavedLocationsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MyWeatherTheme {
                SystemBarsColor()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ProvideWindowInsets {
                        WeatherApp()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
    @Composable
    fun WeatherApp(){
        val navHostEngine = rememberAnimatedNavHostEngine(
            navHostContentAlignment = Alignment.TopCenter,
            rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING
        )
        val navController = navHostEngine.rememberNavController()

        val startLocation = if(savedLocationsRepository.getFirstRun()){
            WelcomeScreenDestination
        } else {
            HomeScreenDestination
        }

        DestinationsNavHost(
            engine = navHostEngine,
            navController = navController,
            navGraph = NavGraphs.root,
            startRoute = startLocation
        )
    }

    @Composable
    private fun SystemBarsColor() {
        val systemUiController = rememberSystemUiController()
        val useDarkIcons = MaterialTheme.colors.isLight
        SideEffect {
            systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = useDarkIcons)
        }
    }
}