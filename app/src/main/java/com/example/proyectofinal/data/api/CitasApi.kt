package com.example.proyectofinal.data.api

import com.example.proyectofinal.data.models.CitaDto
import retrofit2.http.*

interface CitasApi {
    @GET("api/citas")
    suspend fun getCitas(): List<CitaDto>

    @GET("api/citas/{id}")
    suspend fun getCita(@Path("id") id: Int): CitaDto

    @POST("api/citas")
    suspend fun createCita(@Body cita: CitaDto): CitaDto

    @PUT("api/citas/{id}")
    suspend fun updateCita(@Path("id") id: Int, @Body cita: CitaDto): CitaDto

    @DELETE("api/citas/{id}")
    suspend fun deleteCita(@Path("id") id: Int): Unit

    @PATCH("api/citas/{id}/estado")
    suspend fun updateEstado(@Path("id") id: Int, @Body estado: String): CitaDto
}
