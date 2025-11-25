package com.example.proyectofinal.ui.perfil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.proyectofinal.data.api.RetrofitClient
import com.example.proyectofinal.databinding.FragmentPerfilBinding
import com.example.proyectofinal.ui.login.LoginActivity

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserData()
        setupButtons()
    }

    private fun loadUserData() {
        val tokenManager = RetrofitClient.getTokenManager()

        val displayName = tokenManager.nombreCompleto ?: tokenManager.username ?: "Usuario"
        binding.tvNombreCompleto.text = displayName
        binding.tvUsername.text = tokenManager.username ?: "N/A"
        binding.tvRol.text = tokenManager.userRole ?: "N/A"
        binding.tvEmail.text = "usuario@hospital.com" // TODO: Get from API
        binding.tvFechaCreacion.text = "2025-01-15" // TODO: Get from API
    }

    private fun setupButtons() {
        binding.btnCerrarSesion.setOnClickListener { showLogoutDialog() }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                logout()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun logout() {
        RetrofitClient.getTokenManager().clear()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
