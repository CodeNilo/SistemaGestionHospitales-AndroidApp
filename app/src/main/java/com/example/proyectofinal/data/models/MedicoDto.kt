package com.example.proyectofinal.data.models

import com.squareup.moshi.Json

data class MedicoDto(
    @Json(name = "id")
    val id: Int,

    @Json(name = "nombres")
    val nombres: String,

    @Json(name = "apellidos")
    val apellidos: String,

    @Json(name = "nombreCompleto")
    val nombreCompleto: String,

    @Json(name = "email")
    val email: String,

    @Json(name = "duracionCitaMinutos")
    val duracionCitaMinutos: Int = 30
)
