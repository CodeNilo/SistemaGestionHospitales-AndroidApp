package com.example.proyectofinal.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.BuildConfig
import com.example.proyectofinal.data.TokenManager
import com.example.proyectofinal.data.api.AuthApi
import com.example.proyectofinal.data.models.RegisterRequest
import com.example.proyectofinal.data.models.UsuarioDto
import kotlinx.coroutines.launch

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val user: UsuarioDto) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableLiveData<RegisterState>(RegisterState.Idle)
    val state: LiveData<RegisterState> = _state

    fun register(username: String, password: String, nombres: String, apellidos: String, email: String?) {
        _state.value = RegisterState.Loading

        viewModelScope.launch {
            try {
                val request = RegisterRequest(
                    username = username,
                    password = password,
                    nombres = nombres,
                    apellidos = apellidos,
                    email = email
                )

                val response = authApi.register(request)

                tokenManager.token = response.token
                tokenManager.userId = response.user.id
                tokenManager.username = response.user.username
                tokenManager.userRole = response.user.rol
                tokenManager.nombreCompleto = response.user.nombreCompleto

                _state.value = RegisterState.Success(response.user)
            } catch (e: Exception) {
                val baseUrl = BuildConfig.BASE_URL
                val msg = when {
                    e.message?.contains("400") == true -> "Datos invalidos o usuario ya existe"
                    e.message?.contains("Unable to resolve host") == true -> "Sin conexion al servidor ($baseUrl)"
                    e.message?.contains("Failed to connect") == true -> "No se pudo conectar al servidor ($baseUrl)"
                    else -> "Error: ${e.message ?: "Desconocido"}"
                }
                _state.value = RegisterState.Error(msg)
            }
        }
    }
}

class RegisterViewModelFactory(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(authApi, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
