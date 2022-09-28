package com.filippachucki.myweather.repository

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dev.zacsweers.moshix.sealed.reflect.MoshiSealedJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RetrofitApiProvider @Inject constructor() {

    companion object {
        private const val DEFAULT_TIMEOUT = 60L
    }

    /* JSON to Data Class converter */
    private val moshi = Moshi.Builder()
            .add(MoshiSealedJsonAdapterFactory())
            .add(KotlinJsonAdapterFactory())
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .build()

    /* HTTP Client */
    private val httpClient = OkHttpClient().newBuilder()
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .followRedirects(false)
            .followSslRedirects(false)
            .addInterceptor(HttpLoggingInterceptor().setLevel(Level.BODY)) // Logging interceptor
            .build()

    fun provideWeatherApi(): WeatherApi {
        return Retrofit.Builder()
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .baseUrl("https://api.openweathermap.org")
            .build()
            .create(WeatherApi::class.java)
    }
}
