package com.bservan.apps.contacts.domain.net.http

import com.bservan.apps.contacts.BuildConfig
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ContactApiClient {
    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        loggingInterceptor.redactHeader("ApiKey")

        OkHttpClient.Builder()
            .connectionPool(ConnectionPool(
                maxIdleConnections = 10,
                keepAliveDuration = 5,
                TimeUnit.MINUTES
            ))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(postRequestHeadersInterceptor)
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)
            .build()
    }

    private val apiKeyInterceptor by lazy {
        ContactApiKeyInterceptor(BuildConfig.API_KEY)
    }

    private val postRequestHeadersInterceptor by lazy {
        ContactPostRequestAddHeaderInterceptor()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: ContactService by lazy {
        retrofit.create(ContactService::class.java)
    }
}