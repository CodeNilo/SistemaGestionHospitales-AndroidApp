package com.example.proyectofinal.ui.citas

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinal.data.api.RetrofitClient
import com.example.proyectofinal.databinding.FragmentCitasBinding

class CitasFragment : Fragment() {

    private var _binding: FragmentCitasBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CitasViewModel by viewModels {
        CitasViewModelFactory(RetrofitClient.citasApi)
    }

    private lateinit var adapter: CitasAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCitasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupSwipeRefresh()
        setupFAB()

        viewModel.loadCitas()
    }

    private fun setupRecyclerView() {
        adapter = CitasAdapter { cita ->
            // Navegar al detalle de la cita
            val intent = Intent(requireContext(), CitaDetailActivity::class.java)
            intent.putExtra("CITA_ID", cita.id)
            startActivity(intent)
        }

        binding.rvCitas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCitas.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.citasState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CitasState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvCitas.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                }
                is CitasState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    binding.tvError.visibility = View.GONE

                    if (state.citas.isEmpty()) {
                        binding.rvCitas.visibility = View.GONE
                        binding.tvEmpty.visibility = View.VISIBLE
                    } else {
                        binding.rvCitas.visibility = View.VISIBLE
                        binding.tvEmpty.visibility = View.GONE
                        adapter.submitList(state.citas)
                    }
                }
                is CitasState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    binding.rvCitas.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvError.text = state.message
                }
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadCitas()
        }
    }

    private fun setupFAB() {
        binding.fabNuevaCita.setOnClickListener {
            val intent = Intent(requireContext(), CreateCitaActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar citas al volver
        viewModel.loadCitas()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
