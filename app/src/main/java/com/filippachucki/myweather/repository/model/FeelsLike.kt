package com.filippachucki.myweather.repository.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeelsLike (
	val day : Double,
	val night : Double,
	val eve : Double,
	val morn : Double
) : Parcelable