package com.example.proyectofinal.ui.triaje

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.data.api.RetrofitClient
import com.example.proyectofinal.data.models.TriajeDto
import kotlinx.coroutines.launch

class TriajeViewModel : ViewModel() {

    private val _triaje = MutableLiveData<TriajeDto?>()
    val triaje: LiveData<TriajeDto?> = _triaje

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    fun loadTriajeByCita(citaId: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = RetrofitClient.triajeApi.getTriajeByCitaId(citaId)
                _triaje.value = response
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                // Si no existe triaje, no es error, simplemente no hay datos
                _triaje.value = null
            }
        }
    }

    fun saveTriaje(
        citaId: Int,
        presionArterial: String?,
        temperatura: Double?,
        frecuenciaCardiaca: Int?,
        frecuenciaRespiratoria: Int?,
        saturacionOxigeno: Int?,
        peso: Double?,
        altura: Double?,
        glucemia: Double?,
        motivo: String?,
        observaciones: String?
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true

                val triajeDto = TriajeDto(
                    id = _triaje.value?.id ?: 0,
                    citaId = citaId,
                    presionArterial = presionArterial,
                    temperatura = temperatura,
                    frecuenciaCardiaca = frecuenciaCardiaca,
                    frecuenciaRespiratoria = frecuenciaRespiratoria,
                    saturacionOxigeno = saturacionOxigeno,
                    peso = peso,
                    altura = altura,
                    glucemiaCapilar = glucemia,
                    motivo = motivo,
                    observaciones = observaciones,
                    estado = "Completado"
                )

                if (_triaje.value?.id != null && _triaje.value?.id != 0) {
                    // Actualizar triaje existente
                    RetrofitClient.triajeApi.updateTriaje(_triaje.value!!.id, triajeDto)
                } else {
                    // Crear nuevo triaje
                    RetrofitClient.triajeApi.createTriaje(triajeDto)
                }

                _loading.value = false
                _success.value = true
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Error al guardar triaje: ${e.message}"
            }
        }
    }
}
