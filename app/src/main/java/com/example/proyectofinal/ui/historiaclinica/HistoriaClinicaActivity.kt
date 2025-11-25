package com.example.proyectofinal.ui.historiaclinica

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal.data.api.RetrofitClient
import com.example.proyectofinal.databinding.ActivityHistoriaClinicaBinding
import com.google.android.material.snackbar.Snackbar

class HistoriaClinicaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoriaClinicaBinding
    private val viewModel: HistoriaClinicaViewModel by viewModels()
    private var pacienteId: Int = 0
    private var pacienteNombre: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoriaClinicaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pacienteId = intent.getIntExtra("PACIENTE_ID", 0)
        pacienteNombre = intent.getStringExtra("PACIENTE_NOMBRE") ?: "Paciente"

        if (pacienteId == 0) {
            Toast.makeText(this, "Error: ID de paciente no valido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupUI()
        setupObservers()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupUI() {
        binding.tvPacienteNombre.text = pacienteNombre
    }

    private fun setupObservers() {
        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.contentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.success.observe(this) { success ->
            if (success == true) {
                Toast.makeText(this, "Historia clinica guardada correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupListeners() {
        binding.btnGuardar.setOnClickListener {
            if (validateForm()) {
                saveHistoriaClinica()
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (binding.etMotivoConsulta.text.isNullOrBlank()) {
            binding.etMotivoConsulta.error = "El motivo de consulta es obligatorio"
            isValid = false
        }

        if (binding.etDiagnostico.text.isNullOrBlank()) {
            binding.etDiagnostico.error = "El diagnostico es obligatorio"
            isValid = false
        }

        if (binding.etTratamiento.text.isNullOrBlank()) {
            binding.etTratamiento.error = "El tratamiento es obligatorio"
            isValid = false
        }

        return isValid
    }

    private fun saveHistoriaClinica() {
        val medicoId = RetrofitClient.getTokenManager().userId ?: 0

        val motivoConsulta = binding.etMotivoConsulta.text.toString()
        val diagnostico = binding.etDiagnostico.text.toString()
        val tratamiento = binding.etTratamiento.text.toString()
        val observaciones = binding.etObservaciones.text?.toString()
        val presionArterial = binding.etPresionArterial.text?.toString()
        val temperatura = binding.etTemperatura.text?.toString()?.toDoubleOrNull()
        val peso = binding.etPeso.text?.toString()?.toDoubleOrNull()
        val altura = binding.etAltura.text?.toString()?.toDoubleOrNull()

        viewModel.createHistoriaClinica(
            pacienteId = pacienteId,
            medicoId = medicoId,
            motivoConsulta = motivoConsulta,
            diagnostico = diagnostico,
            tratamiento = tratamiento,
            observaciones = observaciones,
            presionArterial = presionArterial,
            temperatura = temperatura,
            peso = peso,
            altura = altura
        )
    }
}
