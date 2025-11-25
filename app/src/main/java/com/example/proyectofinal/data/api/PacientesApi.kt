package com.example.proyectofinal.data.api

import com.example.proyectofinal.data.models.PacienteDto
import retrofit2.http.*

interface PacientesApi {
    @GET("api/pacientes")
    suspend fun getPacientes(): List<PacienteDto>

    @GET("api/pacientes/{id}")
    suspend fun getPaciente(@Path("id") id: Int): PacienteDto

    @GET("api/pacientes/usuario/{usuarioId}")
    suspend fun getPacienteByUsuarioId(@Path("usuarioId") usuarioId: Int): PacienteDto

    @GET("api/pacientes/search")
    suspend fun searchPacientes(@Query("term") term: String): List<PacienteDto>
}
