package com.example.proyectofinal.data.api

import com.example.proyectofinal.data.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenManager.token

        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        val requestWithToken = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(requestWithToken)
    }
}
