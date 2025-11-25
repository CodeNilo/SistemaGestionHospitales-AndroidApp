package com.example.proyectofinal.data.models

import com.squareup.moshi.Json

data class LoginResponse(
    @Json(name = "token")
    val token: String,

    @Json(name = "user")
    val user: UsuarioDto
)
