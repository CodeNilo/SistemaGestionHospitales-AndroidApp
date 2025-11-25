package com.example.proyectofinal.data.models

import com.squareup.moshi.Json

data class TriajeDto(
    @Json(name = "id")
    val id: Int = 0,

    @Json(name = "citaId")
    val citaId: Int,

    @Json(name = "pacienteId")
    val pacienteId: Int? = null,

    @Json(name = "pacienteNombre")
    val pacienteNombre: String? = null,

    @Json(name = "medicoNombre")
    val medicoNombre: String? = null,

    @Json(name = "motivo")
    val motivo: String? = null,

    @Json(name = "observaciones")
    val observaciones: String? = null,

    @Json(name = "presionArterial")
    val presionArterial: String? = null,

    @Json(name = "frecuenciaCardiaca")
    val frecuenciaCardiaca: Int? = null,

    @Json(name = "frecuenciaRespiratoria")
    val frecuenciaRespiratoria: Int? = null,

    @Json(name = "temperatura")
    val temperatura: Double? = null,

    @Json(name = "saturacionOxigeno")
    val saturacionOxigeno: Int? = null,

    @Json(name = "peso")
    val peso: Double? = null,

    @Json(name = "altura")
    val altura: Double? = null,

    @Json(name = "glucemiaCapilar")
    val glucemiaCapilar: Double? = null,

    @Json(name = "estado")
    val estado: String = "Completado"
)
