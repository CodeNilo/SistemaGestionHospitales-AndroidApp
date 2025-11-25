package com.example.proyectofinal.data.api

import com.example.proyectofinal.data.models.HistoriaClinicaDto
import retrofit2.http.*

interface HistoriaClinicaApi {
    @GET("api/historia-clinica/paciente/{pacienteId}")
    suspend fun getHistoriasByPacienteId(@Path("pacienteId") pacienteId: Int): List<HistoriaClinicaDto>

    @GET("api/historia-clinica/medico/{medicoId}")
    suspend fun getHistoriasByMedicoId(@Path("medicoId") medicoId: Int): List<HistoriaClinicaDto>

    @GET("api/historia-clinica/{id}")
    suspend fun getHistoria(@Path("id") id: Int): HistoriaClinicaDto

    @POST("api/historia-clinica")
    suspend fun createHistoria(@Body historia: HistoriaClinicaDto): HistoriaClinicaDto

    @PUT("api/historia-clinica/{id}")
    suspend fun updateHistoria(@Path("id") id: Int, @Body historia: HistoriaClinicaDto): HistoriaClinicaDto

    @GET("api/historia-clinica/search")
    suspend fun searchHistorias(@Query("term") term: String): List<HistoriaClinicaDto>
}
