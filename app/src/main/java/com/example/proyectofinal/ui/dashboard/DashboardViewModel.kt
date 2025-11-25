package com.example.proyectofinal.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.data.api.RetrofitClient
import com.example.proyectofinal.data.models.CitaDto
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val _citas = MutableLiveData<List<CitaDto>>()
    val citas: LiveData<List<CitaDto>> = _citas

    private val _citasHoy = MutableLiveData<Int>()
    val citasHoy: LiveData<Int> = _citasHoy

    private val _citasMes = MutableLiveData<Int>()
    val citasMes: LiveData<Int> = _citasMes

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadDashboardData() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val allCitas = RetrofitClient.citasApi.getCitas()

                // Filtrar citas próximas (tomar las primeras 5)
                val proximasCitas = allCitas
                    .filter { it.estado != "Cancelada" }
                    .sortedBy { it.fechaCita }
                    .take(5)

                _citas.value = proximasCitas

                // Calcular estadísticas
                val today = java.time.LocalDate.now().toString()
                val citasHoyCount = allCitas.count { it.fechaCita == today && it.estado != "Cancelada" }
                _citasHoy.value = citasHoyCount

                // Citas del mes actual
                val currentMonth = today.substring(0, 7) // yyyy-MM
                val citasMesCount = allCitas.count {
                    it.fechaCita.startsWith(currentMonth) && it.estado != "Cancelada"
                }
                _citasMes.value = citasMesCount

                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Error al cargar dashboard: ${e.message}"
            }
        }
    }

    fun getEstadisticasPorRol(userRole: String): Pair<String, String> {
        return when (userRole) {
            "Paciente" -> Pair("Próximas Citas", "Total Este Mes")
            "Medico" -> Pair("Citas Hoy", "Pacientes Este Mes")
            "Administrador", "Administrativo" -> Pair("Citas Hoy", "Total Este Mes")
            "Enfermeria" -> Pair("Triajes Hoy", "Total Este Mes")
            else -> Pair("Citas", "Total")
        }
    }

    fun getAccionesPorRol(userRole: String): List<String> {
        return when (userRole) {
            "Paciente" -> listOf("Agendar Nueva Cita", "Ver Mi Historial")
            "Medico" -> listOf("Ver Citas del Día", "Iniciar Triaje", "Ver Historias Clínicas")
            "Administrador", "Administrativo" -> listOf("Nueva Cita", "Ver Todas las Citas", "Gestionar Usuarios")
            "Enfermeria" -> listOf("Iniciar Triaje", "Ver Citas del Día")
            else -> listOf("Ver Citas")
        }
    }
}
