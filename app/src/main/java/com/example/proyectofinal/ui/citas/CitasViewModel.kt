package com.example.proyectofinal.ui.citas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.data.api.CitasApi
import com.example.proyectofinal.data.models.CitaDto
import kotlinx.coroutines.launch

sealed class CitasState {
    object Loading : CitasState()
    data class Success(val citas: List<CitaDto>) : CitasState()
    data class Error(val message: String) : CitasState()
}

class CitasViewModel(private val citasApi: CitasApi) : ViewModel() {

    private val _citasState = MutableLiveData<CitasState>()
    val citasState: LiveData<CitasState> = _citasState

    fun loadCitas() {
        _citasState.value = CitasState.Loading

        viewModelScope.launch {
            try {
                val citas = citasApi.getCitas()
                _citasState.value = CitasState.Success(citas)
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("401") == true -> "Sesión expirada. Inicie sesión nuevamente."
                    e.message?.contains("Unable to resolve host") == true -> "Sin conexión al servidor"
                    e.message?.contains("Failed to connect") == true -> "No se pudo conectar al servidor"
                    else -> "Error: ${e.message ?: "Desconocido"}"
                }
                _citasState.value = CitasState.Error(errorMessage)
            }
        }
    }
}

class CitasViewModelFactory(private val citasApi: CitasApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CitasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CitasViewModel(citasApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
