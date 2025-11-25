package com.example.proyectofinal.data.api

import com.example.proyectofinal.data.models.LoginRequest
import com.example.proyectofinal.data.models.LoginResponse
import com.example.proyectofinal.data.models.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): LoginResponse
}
