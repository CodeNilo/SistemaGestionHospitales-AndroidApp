package com.example.proyectofinal.ui.citas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal.R
import com.example.proyectofinal.data.api.RetrofitClient
import com.example.proyectofinal.data.models.MedicoDto
import com.example.proyectofinal.data.models.PacienteDto
import com.example.proyectofinal.databinding.ActivityCreateCitaBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class CreateCitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateCitaBinding
    private val viewModel: CreateCitaViewModel by viewModels()

    private var selectedMedicoId: Int? = null
    private var selectedPacienteId: Int? = null
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupObservers()
        setupListeners()

        // Cargar datos iniciales
        viewModel.loadMedicos()

        // Mostrar selector de paciente solo para admin/administrativo
        val userRole = RetrofitClient.getTokenManager().userRole
        if (userRole == "Administrador" || userRole == "Administrativo") {
            binding.layoutPaciente.visibility = View.VISIBLE
            viewModel.loadPacientes()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.medicos.observe(this) { medicos ->
            setupMedicosSpinner(medicos)
        }

        viewModel.pacientes.observe(this) { pacientes ->
            setupPacientesSpinner(pacientes)
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
                Toast.makeText(this, "Cita creada correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupListeners() {
        // DatePicker para fecha
        binding.etFecha.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    binding.etFecha.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // TimePicker para hora inicio
        binding.etHoraInicio.setOnClickListener {
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    binding.etHoraInicio.setText(timeFormat.format(calendar.time))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        // TimePicker para hora fin
        binding.etHoraFin.setOnClickListener {
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    binding.etHoraFin.setText(timeFormat.format(calendar.time))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        // Botón guardar
        binding.btnGuardar.setOnClickListener {
            if (validateForm()) {
                createCita()
            }
        }
    }

    private fun setupMedicosSpinner(medicos: List<MedicoDto>) {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            medicos.map { it.nombreCompleto }
        )
        binding.spinnerMedico.setAdapter(adapter)
        binding.spinnerMedico.setOnItemClickListener { _, _, position, _ ->
            selectedMedicoId = medicos[position].id
        }
    }

    private fun setupPacientesSpinner(pacientes: List<PacienteDto>) {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            pacientes.map { it.nombreCompleto }
        )
        binding.spinnerPaciente.setAdapter(adapter)
        binding.spinnerPaciente.setOnItemClickListener { _, _, position, _ ->
            selectedPacienteId = pacientes[position].id
        }
    }

    private fun validateForm(): Boolean {
        if (selectedMedicoId == null) {
            Snackbar.make(binding.root, "Debe seleccionar un médico", Snackbar.LENGTH_SHORT).show()
            return false
        }

        val userRole = RetrofitClient.getTokenManager().userRole
        if ((userRole == "Administrador" || userRole == "Administrativo") && selectedPacienteId == null) {
            Snackbar.make(binding.root, "Debe seleccionar un paciente", Snackbar.LENGTH_SHORT).show()
            return false
        }

        if (binding.etFecha.text.isNullOrBlank()) {
            binding.etFecha.error = "Debe seleccionar una fecha"
            return false
        }

        if (binding.etHoraInicio.text.isNullOrBlank()) {
            binding.etHoraInicio.error = "Debe seleccionar hora de inicio"
            return false
        }

        if (binding.etHoraFin.text.isNullOrBlank()) {
            binding.etHoraFin.error = "Debe seleccionar hora de fin"
            return false
        }

        if (binding.etMotivo.text.isNullOrBlank()) {
            binding.etMotivo.error = "Debe ingresar el motivo"
            return false
        }

        return true
    }

    private fun createCita() {
        val userRole = RetrofitClient.getTokenManager().userRole
        val pacienteId = if (userRole == "Paciente") {
            // Para pacientes, obtener su ID desde su perfil
            RetrofitClient.getTokenManager().userId ?: 0
        } else {
            selectedPacienteId ?: 0
        }

        viewModel.createCita(
            pacienteId = pacienteId,
            medicoId = selectedMedicoId!!,
            fechaCita = binding.etFecha.text.toString(),
            horaInicio = binding.etHoraInicio.text.toString(),
            horaFin = binding.etHoraFin.text.toString(),
            motivo = binding.etMotivo.text.toString(),
            observaciones = binding.etObservaciones.text?.toString()
        )
    }
}
