package com.filippachucki.myweather.repository

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocationProvider @Inject constructor(@ApplicationContext context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(onSuccess: (Double, Double) -> Unit, onFailure: (Exception) -> Unit) {
        fusedLocationClient
            .getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) = this
                    override fun isCancellationRequested(): Boolean = false
                }
            )
            .addOnFailureListener { onFailure(it) }
            .addOnSuccessListener { location -> onSuccess(location.latitude, location.longitude) }
    }
}