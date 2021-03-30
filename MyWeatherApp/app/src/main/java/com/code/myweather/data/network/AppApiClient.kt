package com.code.myweather.data.network

import com.code.myweather.data.network.responses.*
import com.code.myweather.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface AppApiClient {

    @GET("forecast?")
    suspend fun getForecastByGPS(
            @Query("lat") latitude: String,
            @Query("lon") longitude: String,
            @Query("cnt") cnt: String,
            @Query("units") units: String): Response<ForecastResponse>

    companion object {
        operator fun invoke(
                networkConnectionInterceptor: NetworkConnectionInterceptor,
        ): AppApiClient {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(networkConnectionInterceptor)
                    .addInterceptor(RequestInterceptor())
                    .addInterceptor(httpLoggingInterceptor)
                    .build()
            return Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(AppApiClient::class.java)
        }
    }
}