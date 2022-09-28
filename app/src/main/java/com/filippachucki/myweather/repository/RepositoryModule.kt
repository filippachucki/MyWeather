package com.filippachucki.myweather.repository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class RepositoryModule {

    @Provides
    @Singleton
    fun provideWeatherApi(apiProvider: RetrofitApiProvider): WeatherApi =
        apiProvider.provideWeatherApi()
}