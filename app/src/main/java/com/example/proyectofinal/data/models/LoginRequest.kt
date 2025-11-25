package com.example.proyectofinal.data.models

import com.squareup.moshi.Json

data class LoginRequest(
    @Json(name = "username")
    val username: String,

    @Json(name = "password")
    val password: String
)
