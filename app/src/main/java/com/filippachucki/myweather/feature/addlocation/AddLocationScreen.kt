package com.filippachucki.myweather.feature.addlocation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.filippachucki.myweather.repository.model.Location
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination
@Composable
fun AddLocationScreen(
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<Location>,
    viewModel: AddLocationViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    Column(
        Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            IconButton(onClick = { navigator.navigateUp() }) {
                Icon(Icons.Default.ArrowBack, null)
            }
            Text("Add location", style = MaterialTheme.typography.h6)
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            value = uiState.input,
            label = { Text("Location name") },
            onValueChange = { viewModel.onLocationNameChanged(it) },
            trailingIcon = {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                }
            }
        )
        LazyColumn(contentPadding = PaddingValues(24.dp)) {
            items(uiState.locations) {
                SearchItemRow(
                    modifier = Modifier.clickable { resultNavigator.navigateBack(it) },
                    item = it
                )
            }
        }
    }
}

@Composable
private fun SearchItemRow(
    modifier: Modifier = Modifier,
    item: Location
) = Row(
    modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween
) {
    Column {
        Text(item.name)
        Text(
            "${item.lat} ${item.lon}",
            modifier = Modifier.alpha(0.5f),
            style = MaterialTheme.typography.subtitle2
        )
    }
    Text(item.country)
}
