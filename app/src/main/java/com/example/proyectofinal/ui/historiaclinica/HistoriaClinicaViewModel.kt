package com.example.proyectofinal.ui.historiaclinica

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.data.api.RetrofitClient
import com.example.proyectofinal.data.models.HistoriaClinicaDto
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HistoriaClinicaViewModel : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    fun createHistoriaClinica(
        pacienteId: Int,
        medicoId: Int,
        motivoConsulta: String,
        diagnostico: String,
        tratamiento: String,
        observaciones: String?,
        presionArterial: String?,
        temperatura: Double?,
        peso: Double?,
        altura: Double?
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true

                val fechaAtencion = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date())

                val historiaDto = HistoriaClinicaDto(
                    id = 0,
                    pacienteId = pacienteId,
                    medicoId = medicoId,
                    fechaAtencion = fechaAtencion,
                    motivoConsulta = motivoConsulta,
                    diagnostico = diagnostico,
                    tratamiento = tratamiento,
                    observaciones = observaciones,
                    presionArterial = presionArterial,
                    temperatura = temperatura,
                    peso = peso,
                    altura = altura
                )

                RetrofitClient.historiaClinicaApi.createHistoria(historiaDto)

                _loading.value = false
                _success.value = true
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Error al guardar historia clínica: ${e.message}"
            }
        }
    }
}
