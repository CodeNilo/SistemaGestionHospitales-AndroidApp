package com.example.proyectofinal.ui.citas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.data.api.RetrofitClient
import com.example.proyectofinal.data.models.CitaDto
import com.example.proyectofinal.data.models.MedicoDto
import com.example.proyectofinal.data.models.PacienteDto
import kotlinx.coroutines.launch

class CreateCitaViewModel : ViewModel() {

    private val _medicos = MutableLiveData<List<MedicoDto>>()
    val medicos: LiveData<List<MedicoDto>> = _medicos

    private val _pacientes = MutableLiveData<List<PacienteDto>>()
    val pacientes: LiveData<List<PacienteDto>> = _pacientes

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    fun loadMedicos() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = RetrofitClient.medicosApi.getMedicos()
                _medicos.value = response
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Error al cargar médicos: ${e.message}"
            }
        }
    }

    fun loadPacientes() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = RetrofitClient.pacientesApi.getPacientes()
                _pacientes.value = response
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Error al cargar pacientes: ${e.message}"
            }
        }
    }

    fun createCita(
        pacienteId: Int,
        medicoId: Int,
        fechaCita: String,
        horaInicio: String,
        horaFin: String,
        motivo: String,
        observaciones: String?
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val cita = CitaDto(
                    id = 0,
                    pacienteId = pacienteId,
                    medicoId = medicoId,
                    fechaCita = fechaCita,
                    horaInicio = horaInicio,
                    horaFin = horaFin,
                    estado = "Pendiente",
                    motivo = motivo,
                    observaciones = observaciones,
                    pacienteNombre = null,
                    medicoNombre = null
                )
                RetrofitClient.citasApi.createCita(cita)
                _loading.value = false
                _success.value = true
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Error al crear cita: ${e.message}"
            }
        }
    }
}
