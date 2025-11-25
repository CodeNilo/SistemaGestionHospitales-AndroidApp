package com.example.proyectofinal.data.models

import com.squareup.moshi.Json

data class HistoriaClinicaDto(
    @Json(name = "id")
    val id: Int = 0,

    @Json(name = "pacienteId")
    val pacienteId: Int,

    @Json(name = "medicoId")
    val medicoId: Int,

    @Json(name = "fechaAtencion")
    val fechaAtencion: String,

    @Json(name = "motivoConsulta")
    val motivoConsulta: String,

    @Json(name = "diagnostico")
    val diagnostico: String,

    @Json(name = "tratamiento")
    val tratamiento: String,

    @Json(name = "observaciones")
    val observaciones: String? = null,

    @Json(name = "presionArterial")
    val presionArterial: String? = null,

    @Json(name = "temperatura")
    val temperatura: Double? = null,

    @Json(name = "peso")
    val peso: Double? = null,

    @Json(name = "altura")
    val altura: Double? = null,

    @Json(name = "pacienteNombre")
    val pacienteNombre: String? = null,

    @Json(name = "pacienteDNI")
    val pacienteDNI: String? = null,

    @Json(name = "medicoNombre")
    val medicoNombre: String? = null,

    @Json(name = "citaId")
    val citaId: Int? = null
)
