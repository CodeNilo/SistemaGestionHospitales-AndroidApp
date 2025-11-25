package com.example.proyectofinal.data.models

import com.squareup.moshi.Json

data class UsuarioDto(
    @Json(name = "id")
    val id: Int,

    @Json(name = "username")
    val username: String,

    @Json(name = "rol")
    val rol: String,

    @Json(name = "nombreCompleto")
    val nombreCompleto: String
)
