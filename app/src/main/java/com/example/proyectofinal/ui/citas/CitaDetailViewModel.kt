package com.example.proyectofinal.ui.citas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.data.api.RetrofitClient
import com.example.proyectofinal.data.models.CitaDto
import com.example.proyectofinal.data.models.TriajeDto
import kotlinx.coroutines.launch

class CitaDetailViewModel : ViewModel() {

    private val _cita = MutableLiveData<CitaDto>()
    val cita: LiveData<CitaDto> = _cita

    private val _triaje = MutableLiveData<TriajeDto?>()
    val triaje: LiveData<TriajeDto?> = _triaje

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _success = MutableLiveData<String>()
    val success: LiveData<String> = _success

    fun loadCita(citaId: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = RetrofitClient.citasApi.getCita(citaId)
                _cita.value = response
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Error al cargar la cita: ${e.message}"
            }
        }
    }

    fun loadTriaje(citaId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.triajeApi.getTriajeByCitaId(citaId)
                _triaje.value = response
            } catch (e: Exception) {
                // Si no hay triaje o error, ocultamos la tarjeta
                _triaje.value = null
            }
        }
    }

    fun cancelarCita(citaId: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                RetrofitClient.citasApi.updateEstado(citaId, "Cancelada")
                _success.value = "Cita cancelada correctamente"
                loadCita(citaId) // Recargar la cita
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Error al cancelar la cita: ${e.message}"
            }
        }
    }

    fun completarCita(citaId: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                RetrofitClient.citasApi.updateEstado(citaId, "Atendida")
                _success.value = "Cita completada correctamente"
                loadCita(citaId) // Recargar la cita
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Error al completar la cita: ${e.message}"
            }
        }
    }

    fun eliminarCita(citaId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _loading.value = true
                RetrofitClient.citasApi.deleteCita(citaId)
                _loading.value = false
                _success.value = "Cita eliminada correctamente"
                onSuccess()
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Error al eliminar la cita: ${e.message}"
            }
        }
    }
}
