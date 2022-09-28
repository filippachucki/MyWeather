package com.filippachucki.myweather.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import com.filippachucki.myweather.repository.model.Location
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.reflect.Type
import javax.inject.Inject

class SavedLocationsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val type: Type = Types.newParameterizedType(List::class.java, Location::class.java)
    private val adapter: JsonAdapter<List<Location>> = moshi.adapter(type)

    fun setFirstRun(enabled: Boolean) = preferences.edit { putBoolean(PREF_FIRST_RUN, enabled) }

    fun getFirstRun() : Boolean = preferences.getBoolean(PREF_FIRST_RUN, true)

    fun getLocations(): List<Location> = preferences
        .getString(PREF_LOCATIONS, null)
        ?.let { adapter.fromJson(it) }
        ?: emptyList()

    fun addLocation(location: Location) = getLocations()
        .plus(location)
        .also { saveLocations(it) }

    fun removeLocation(location: Location) = getLocations()
        .minus(location)
        .also { saveLocations(it) }

    private fun saveLocations(list: List<Location>) = adapter
        .toJson(list)
        .let { preferences.edit { putString(PREF_LOCATIONS, it) } }

    companion object {
        private const val PREFERENCES_NAME = "SAVED_LOCATIONS"
        private const val PREF_LOCATIONS = "PREF_LOCATIONS"
        private const val PREF_FIRST_RUN = "PREF_FIRST_RUN"

    }
}