package com.example.proyectofinal.data.api

import com.example.proyectofinal.data.models.MedicoDto
import retrofit2.http.GET
import retrofit2.http.Path

interface MedicosApi {
    @GET("api/medicos")
    suspend fun getMedicos(): List<MedicoDto>

    @GET("api/medicos/{id}")
    suspend fun getMedico(@Path("id") id: Int): MedicoDto
}
