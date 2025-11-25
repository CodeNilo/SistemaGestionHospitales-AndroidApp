package com.example.proyectofinal.ui.triaje

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal.databinding.ActivityTriajeBinding
import com.google.android.material.snackbar.Snackbar

class TriajeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTriajeBinding
    private val viewModel: TriajeViewModel by viewModels()
    private var citaId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTriajeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        citaId = intent.getIntExtra("CITA_ID", 0)
        if (citaId == 0) {
            Toast.makeText(this, "Error: ID de cita no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupObservers()
        setupListeners()

        // Cargar triaje existente si hay
        viewModel.loadTriajeByCita(citaId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.triaje.observe(this) { triaje ->
            if (triaje != null) {
                // Cargar datos existentes
                binding.etPresionArterial.setText(triaje.presionArterial)
                binding.etTemperatura.setText(triaje.temperatura?.toString() ?: "")
                binding.etFrecuenciaCardiaca.setText(triaje.frecuenciaCardiaca?.toString() ?: "")
                binding.etFrecuenciaRespiratoria.setText(triaje.frecuenciaRespiratoria?.toString() ?: "")
                binding.etSaturacionOxigeno.setText(triaje.saturacionOxigeno?.toString() ?: "")
                binding.etPeso.setText(triaje.peso?.toString() ?: "")
                binding.etAltura.setText(triaje.altura?.toString() ?: "")
                binding.etGlucemia.setText(triaje.glucemiaCapilar?.toString() ?: "")
                binding.etMotivo.setText(triaje.motivo)
                binding.etObservaciones.setText(triaje.observaciones)
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.formLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.success.observe(this) { success ->
            if (success == true) {
                Toast.makeText(this, "Triaje guardado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupListeners() {
        binding.btnGuardar.setOnClickListener {
            if (validateForm()) {
                saveTriaje()
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validar al menos un campo vital
        val hasVitalSigns = !binding.etPresionArterial.text.isNullOrBlank() ||
                !binding.etTemperatura.text.isNullOrBlank() ||
                !binding.etFrecuenciaCardiaca.text.isNullOrBlank()

        if (!hasVitalSigns) {
            Snackbar.make(
                binding.root,
                "Debe ingresar al menos un signo vital",
                Snackbar.LENGTH_LONG
            ).show()
            isValid = false
        }

        return isValid
    }

    private fun saveTriaje() {
        val presionArterial = binding.etPresionArterial.text?.toString()
        val temperatura = binding.etTemperatura.text?.toString()?.toDoubleOrNull()
        val frecuenciaCardiaca = binding.etFrecuenciaCardiaca.text?.toString()?.toIntOrNull()
        val frecuenciaRespiratoria = binding.etFrecuenciaRespiratoria.text?.toString()?.toIntOrNull()
        val saturacionOxigeno = binding.etSaturacionOxigeno.text?.toString()?.toIntOrNull()
        val peso = binding.etPeso.text?.toString()?.toDoubleOrNull()
        val altura = binding.etAltura.text?.toString()?.toDoubleOrNull()
        val glucemia = binding.etGlucemia.text?.toString()?.toDoubleOrNull()
        val motivo = binding.etMotivo.text?.toString()
        val observaciones = binding.etObservaciones.text?.toString()

        viewModel.saveTriaje(
            citaId = citaId,
            presionArterial = presionArterial,
            temperatura = temperatura,
            frecuenciaCardiaca = frecuenciaCardiaca,
            frecuenciaRespiratoria = frecuenciaRespiratoria,
            saturacionOxigeno = saturacionOxigeno,
            peso = peso,
            altura = altura,
            glucemia = glucemia,
            motivo = motivo,
            observaciones = observaciones
        )
    }
}
