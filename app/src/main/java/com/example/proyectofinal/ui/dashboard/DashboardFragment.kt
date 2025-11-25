package com.example.proyectofinal.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.data.api.RetrofitClient
import com.example.proyectofinal.databinding.FragmentDashboardBinding
import com.example.proyectofinal.ui.citas.CitaDetailActivity
import com.example.proyectofinal.ui.citas.CitasAdapter
import com.example.proyectofinal.ui.citas.CreateCitaActivity
import com.google.android.material.snackbar.Snackbar

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var citasAdapter: CitasAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupRecyclerView()
        setupObservers()
        setupListeners()

        viewModel.loadDashboardData()
    }

    private fun setupUI() {
        val tokenManager = RetrofitClient.getTokenManager()
        val displayName = tokenManager.nombreCompleto ?: tokenManager.username ?: "Usuario"
        val userRole = tokenManager.userRole ?: "Usuario"

        binding.tvNombreUsuario.text = displayName
        binding.tvRol.text = userRole

        // Configurar labels de estadísticas según rol
        val (label1, label2) = viewModel.getEstadisticasPorRol(userRole)
        binding.tvStat1Label.text = label1
        binding.tvStat2Label.text = label2

        // Configurar título de sección
        binding.tvSeccionCitas.text = when (userRole) {
            "Medico" -> "Citas del Día"
            "Administrador", "Administrativo" -> "Citas Recientes"
            else -> "Próximas Citas"
        }

        // Configurar botones de acciones rápidas
        configureQuickActions(userRole)
    }

    private fun setupRecyclerView() {
        citasAdapter = CitasAdapter { cita ->
            // Navegar al detalle de la cita
            val intent = Intent(requireContext(), CitaDetailActivity::class.java)
            intent.putExtra("CITA_ID", cita.id)
            startActivity(intent)
        }

        binding.rvCitasRecientes.apply {
            adapter = citasAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservers() {
        viewModel.citas.observe(viewLifecycleOwner) { citas ->
            if (citas.isEmpty()) {
                binding.rvCitasRecientes.visibility = View.GONE
                binding.tvNoCitas.visibility = View.VISIBLE
            } else {
                binding.rvCitasRecientes.visibility = View.VISIBLE
                binding.tvNoCitas.visibility = View.GONE
                citasAdapter.submitList(citas)
            }
        }

        viewModel.citasHoy.observe(viewLifecycleOwner) { count ->
            binding.tvStat1Value.text = count.toString()
        }

        viewModel.citasMes.observe(viewLifecycleOwner) { count ->
            binding.tvStat2Value.text = count.toString()
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.contentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setupListeners() {
        binding.tvVerTodas.setOnClickListener {
            // Navegar al fragmento de citas
            Toast.makeText(requireContext(), "Navegando a todas las citas...", Toast.LENGTH_SHORT).show()
            // TODO: Implementar navegación
        }
    }

    private fun configureQuickActions(userRole: String) {
        val acciones = viewModel.getAccionesPorRol(userRole)

        // Botón 1
        if (acciones.isNotEmpty()) {
            binding.btnAccion1.text = acciones[0]
            binding.btnAccion1.visibility = View.VISIBLE
            binding.btnAccion1.setOnClickListener {
                when (acciones[0]) {
                    "Agendar Nueva Cita", "Nueva Cita" -> {
                        val intent = Intent(requireContext(), CreateCitaActivity::class.java)
                        startActivity(intent)
                    }
                    "Ver Citas del Día" -> {
                        Toast.makeText(requireContext(), "Ver citas del día", Toast.LENGTH_SHORT).show()
                    }
                    "Iniciar Triaje" -> {
                        Toast.makeText(requireContext(), "Iniciar Triaje - Funcionalidad pendiente", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Botón 2
        if (acciones.size > 1) {
            binding.btnAccion2.text = acciones[1]
            binding.btnAccion2.visibility = View.VISIBLE
            binding.btnAccion2.setOnClickListener {
                Toast.makeText(requireContext(), acciones[1], Toast.LENGTH_SHORT).show()
            }
        }

        // Botón 3
        if (acciones.size > 2) {
            binding.btnAccion3.text = acciones[2]
            binding.btnAccion3.visibility = View.VISIBLE
            binding.btnAccion3.setOnClickListener {
                Toast.makeText(requireContext(), acciones[2], Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar datos al volver al fragment
        viewModel.loadDashboardData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
