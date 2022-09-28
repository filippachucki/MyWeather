package com.filippachucki.myweather.feature.welcome

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.filippachucki.myweather.R
import com.filippachucki.myweather.SetupSystemBars
import com.filippachucki.myweather.feature.destinations.HomeScreenDestination
import com.filippachucki.myweather.feature.destinations.WelcomeScreenDestination
import com.google.android.gms.location.LocationRequest.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Destination
@Composable
fun WelcomeScreen(
    navigator: DestinationsNavigator,
    viewModel: WelcomeScreenViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val wantedPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val state = viewModel.uiState.collectAsState().value
    SetupSystemBars()

    DisposableEffect(Unit) {
        val job = coroutineScope.launch {
            viewModel.navigateToHomeScreen.collect {
                navigator.navigate(HomeScreenDestination) {
                    this.popUpTo(WelcomeScreenDestination) { inclusive = true }
                }
            }
        }
        onDispose { job.cancel() }
    }

    Surface(color = MaterialTheme.colors.secondary) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome", style = MaterialTheme.typography.h2, color = Color.White)
            Spacer(modifier = Modifier.height(24.dp))
            Text(stringResource(id = R.string.welcome_description), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(65.dp))

            if (state.isLoading) {
                CircularProgressIndicator(Modifier.size(64.dp))
            } else {
                JoinButton(wantedPermissions = wantedPermissions) {
                    if (hasPermissions(context, wantedPermissions)) {
                        viewModel.onPermissionGranted()
                    } else {
                        viewModel.onPermissionDenied()
                    }
                }
            }
        }
    }
}

@Composable
private fun JoinButton(wantedPermissions: Array<String>, action: () -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(RequestMultiplePermissions()) {
        action()
    }

    Button(
        modifier = Modifier.size(64.dp),
        shape= CircleShape,
        onClick = {
            if (hasPermissions(context, wantedPermissions)) {
                action()
            } else {
                launcher.launch(wantedPermissions)
            }
        }
    ) {
        Icon(Icons.Default.PlayArrow, null)
    }
}

fun hasPermissions(context: Context, wantedPermissions: Array<String>): Boolean =
    wantedPermissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }