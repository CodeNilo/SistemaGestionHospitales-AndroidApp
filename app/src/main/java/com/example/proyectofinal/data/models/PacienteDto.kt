package com.example.proyectofinal.data.models

import com.squareup.moshi.Json

data class PacienteDto(
    @Json(name = "id")
    val id: Int,

    @Json(name = "dni")
    val dni: String,

    @Json(name = "nombres")
    val nombres: String,

    @Json(name = "apellidos")
    val apellidos: String,

    @Json(name = "nombreCompleto")
    val nombreCompleto: String,

    @Json(name = "fechaNacimiento")
    val fechaNacimiento: String,

    @Json(name = "sexo")
    val sexo: Int,

    @Json(name = "telefono")
    val telefono: String?,

    @Json(name = "email")
    val email: String?,

    @Json(name = "direccion")
    val direccion: String?,

    @Json(name = "grupoSanguineo")
    val grupoSanguineo: String?,

    @Json(name = "alergias")
    val alergias: String?,

    @Json(name = "edad")
    val edad: Int,

    @Json(name = "usuarioId")
    val usuarioId: Int?
)
