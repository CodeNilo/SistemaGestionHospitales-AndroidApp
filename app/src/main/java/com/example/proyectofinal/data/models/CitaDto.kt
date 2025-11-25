package com.example.proyectofinal.data.models

import com.squareup.moshi.Json

data class CitaDto(
    @Json(name = "id")
    val id: Int,

    @Json(name = "pacienteId")
    val pacienteId: Int,

    @Json(name = "medicoId")
    val medicoId: Int,

    @Json(name = "fechaCita")
    val fechaCita: String,

    @Json(name = "horaInicio")
    val horaInicio: String,

    @Json(name = "horaFin")
    val horaFin: String,

    @Json(name = "estado")
    val estado: String,

    @Json(name = "motivo")
    val motivo: String?,

    @Json(name = "observaciones")
    val observaciones: String?,

    @Json(name = "pacienteNombre")
    val pacienteNombre: String?,

    @Json(name = "medicoNombre")
    val medicoNombre: String?
)
