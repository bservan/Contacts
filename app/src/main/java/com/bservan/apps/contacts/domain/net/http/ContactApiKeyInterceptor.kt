package com.bservan.apps.contacts.domain.net.http

import okhttp3.Interceptor
import okhttp3.Response

class ContactApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .addHeader("ApiKey", apiKey)

        return chain.proceed(requestBuilder.build())
    }
}