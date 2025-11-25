package com.example.proyectofinal.data.models

data class RegisterRequest(
    val username: String,
    val password: String,
    val nombres: String,
    val apellidos: String,
    val email: String?
)
