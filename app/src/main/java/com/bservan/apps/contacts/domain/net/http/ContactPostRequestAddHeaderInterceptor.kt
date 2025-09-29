package com.bservan.apps.contacts.domain.net.http

import okhttp3.Interceptor
import okhttp3.Response

class ContactPostRequestAddHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        if (originalRequest.method == "POST" && !originalRequest.url.toString().contains("/UploadImage")) {
            requestBuilder.addHeader("Content-Type", "application/json")
        }

        return chain.proceed(requestBuilder.build())
    }
}
