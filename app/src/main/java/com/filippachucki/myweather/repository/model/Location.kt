package com.filippachucki.myweather.repository.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location (
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String
) : Parcelable
