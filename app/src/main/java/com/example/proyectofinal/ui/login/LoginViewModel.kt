package com.example.proyectofinal.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.BuildConfig
import com.example.proyectofinal.data.TokenManager
import com.example.proyectofinal.data.api.AuthApi
import com.example.proyectofinal.data.models.LoginRequest
import com.example.proyectofinal.data.models.UsuarioDto
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: UsuarioDto) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _loginState

    fun login(username: String, password: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                val request = LoginRequest(username, password)
                val response = authApi.login(request)

                // Guardar token y datos del usuario
                tokenManager.token = response.token
                tokenManager.userId = response.user.id
                tokenManager.username = response.user.username
                tokenManager.userRole = response.user.rol
                tokenManager.nombreCompleto = response.user.nombreCompleto

                _loginState.value = LoginState.Success(response.user)
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("400") == true -> "Credenciales inválidas"
                    e.message?.contains("Unable to resolve host") == true -> "Sin conexión al servidor"
                    e.message?.contains("Failed to connect") == true -> "No se pudo conectar al servidor (${BuildConfig.BASE_URL})"
                    else -> "Error: ${e.message ?: "Desconocido"}"
                }
                _loginState.value = LoginState.Error(errorMessage)
            }
        }
    }
}

class LoginViewModelFactory(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authApi, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
