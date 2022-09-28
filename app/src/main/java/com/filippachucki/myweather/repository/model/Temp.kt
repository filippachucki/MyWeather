package com.filippachucki.myweather.repository.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Temp (
	val day : Double,
	val min : Double,
	val max : Double,
	val night : Double,
	val eve : Double,
	val morn : Double
) : Parcelable