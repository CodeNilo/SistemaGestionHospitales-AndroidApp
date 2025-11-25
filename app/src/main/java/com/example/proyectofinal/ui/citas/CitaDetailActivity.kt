package com.example.proyectofinal.ui.citas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal.R
import com.example.proyectofinal.data.api.RetrofitClient
import com.example.proyectofinal.data.models.TriajeDto
import com.example.proyectofinal.databinding.ActivityCitaDetailBinding
import com.example.proyectofinal.ui.historiaclinica.HistoriaClinicaActivity
import com.example.proyectofinal.ui.triaje.TriajeActivity
import com.google.android.material.snackbar.Snackbar

class CitaDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCitaDetailBinding
    private val viewModel: CitaDetailViewModel by viewModels()
    private var citaId: Int = 0
    private var pacienteId: Int = 0
    private var pacienteNombre: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitaDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        citaId = intent.getIntExtra("CITA_ID", 0)
        if (citaId == 0) {
            Toast.makeText(this, "Error: ID de cita no valido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupObservers()
        setupButtons()

        viewModel.loadCita(citaId)
        viewModel.loadTriaje(citaId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupObservers() {
        viewModel.cita.observe(this) { cita ->
            binding.tvPacienteNombre.text = cita.pacienteNombre ?: "N/A"
            binding.tvMedicoNombre.text = cita.medicoNombre ?: "N/A"
            binding.tvFecha.text = cita.fechaCita
            binding.tvHora.text = "${cita.horaInicio} - ${cita.horaFin}"
            binding.tvMotivo.text = cita.motivo ?: "Sin motivo especificado"
            binding.tvObservaciones.text = cita.observaciones ?: "Sin observaciones"

            pacienteId = cita.pacienteId
            pacienteNombre = cita.pacienteNombre ?: "Paciente"

            binding.tvEstado.text = cita.estado
            binding.tvEstado.setBackgroundResource(getEstadoColor(cita.estado))

            configureButtonsForRole(cita.estado)
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.contentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.success.observe(this) { successMessage ->
            if (successMessage != null) {
                Snackbar.make(binding.root, successMessage, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.triaje.observe(this) { triaje ->
            if (triaje != null) {
                binding.cardTriaje.visibility = View.VISIBLE
                binding.tvTriajeEstado.text = triaje.estado
                binding.tvTriajeResumen.text = buildTriajeResumen(triaje)
            } else {
                binding.cardTriaje.visibility = View.GONE
            }
        }
    }

    private fun setupButtons() {
        binding.btnCancelar.setOnClickListener {
            showConfirmDialog(
                "Cancelar Cita?",
                "Estas seguro de que deseas cancelar esta cita?"
            ) {
                viewModel.cancelarCita(citaId)
            }
        }

        binding.btnCompletar.setOnClickListener {
            showConfirmDialog(
                "Completar Cita?",
                "Estas seguro de que deseas marcar esta cita como completada?"
            ) {
                viewModel.completarCita(citaId)
            }
        }

        binding.btnEliminar.setOnClickListener {
            showConfirmDialog(
                "Eliminar Cita?",
                "Estas seguro de que deseas eliminar esta cita? Esta accion no se puede deshacer."
            ) {
                viewModel.eliminarCita(citaId) {
                    Toast.makeText(this, "Cita eliminada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        binding.btnIniciarTriaje.setOnClickListener {
            val intent = Intent(this, TriajeActivity::class.java)
            intent.putExtra("CITA_ID", citaId)
            startActivity(intent)
        }

        binding.btnHistoriaClinica.setOnClickListener {
            if (pacienteId <= 0) {
                Snackbar.make(binding.root, "No se encontro el paciente de la cita", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val intent = Intent(this, HistoriaClinicaActivity::class.java).apply {
                putExtra("PACIENTE_ID", pacienteId)
                putExtra("PACIENTE_NOMBRE", pacienteNombre)
            }
            startActivity(intent)
        }

        binding.btnEditar.setOnClickListener {
            Toast.makeText(this, "Editar Cita - Funcionalidad pendiente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configureButtonsForRole(estado: String) {
        val userRole = RetrofitClient.getTokenManager().userRole

        binding.btnCancelar.visibility = View.GONE
        binding.btnCompletar.visibility = View.GONE
        binding.btnIniciarTriaje.visibility = View.GONE
        binding.btnHistoriaClinica.visibility = View.GONE
        binding.btnEditar.visibility = View.GONE
        binding.btnEliminar.visibility = View.GONE

        when (userRole) {
            "Paciente" -> {
                if (estado == "Pendiente" || estado == "Confirmada") {
                    binding.btnCancelar.visibility = View.VISIBLE
                }
            }
            "Medico" -> {
                if (estado == "Pendiente" || estado == "Confirmada") {
                    binding.btnCompletar.visibility = View.VISIBLE
                }
                binding.btnHistoriaClinica.visibility = View.VISIBLE
            }
            "Enfermeria" -> {
                binding.btnIniciarTriaje.visibility = View.VISIBLE
            }
            "Administrador", "Administrativo" -> {
                binding.btnEditar.visibility = View.VISIBLE
                binding.btnEliminar.visibility = View.VISIBLE
                if (estado != "Atendida" && estado != "Cancelada") {
                    binding.btnCompletar.visibility = View.VISIBLE
                    binding.btnCancelar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getEstadoColor(estado: String): Int {
        return when (estado) {
            "Pendiente" -> R.color.estado_pendiente
            "Confirmada" -> R.color.estado_confirmada
            "Atendida" -> R.color.estado_atendida
            "Cancelada" -> R.color.estado_cancelada
            else -> R.color.estado_pendiente
        }
    }

    private fun showConfirmDialog(title: String, message: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Si") { _, _ -> onConfirm() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun buildTriajeResumen(triaje: TriajeDto): String {
        val parts = mutableListOf<String>()
        triaje.presionArterial?.let { parts.add("PA: $it") }
        triaje.temperatura?.let { parts.add("Temp: $it") }
        triaje.saturacionOxigeno?.let { parts.add("Sat: $it%") }
        triaje.frecuenciaCardiaca?.let { parts.add("FC: $it") }
        triaje.frecuenciaRespiratoria?.let { parts.add("FR: $it") }
        triaje.peso?.let { parts.add("Peso: $it kg") }
        triaje.altura?.let { parts.add("Altura: $it m") }
        triaje.observaciones?.let { if (it.isNotBlank()) parts.add("Obs: $it") }
        return if (parts.isNotEmpty()) parts.joinToString(", ") else "Triaje registrado"
    }
}



