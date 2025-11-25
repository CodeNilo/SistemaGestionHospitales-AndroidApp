package com.example.proyectofinal.data.api

import com.example.proyectofinal.data.models.TriajeDto
import retrofit2.http.*

interface TriajeApi {
    @GET("api/triaje/cita/{citaId}")
    suspend fun getTriajeByCitaId(@Path("citaId") citaId: Int): TriajeDto

    @POST("api/triaje")
    suspend fun createTriaje(@Body triaje: TriajeDto): TriajeDto

    @PUT("api/triaje/{id}")
    suspend fun updateTriaje(@Path("id") id: Int, @Body triaje: TriajeDto): TriajeDto
}
