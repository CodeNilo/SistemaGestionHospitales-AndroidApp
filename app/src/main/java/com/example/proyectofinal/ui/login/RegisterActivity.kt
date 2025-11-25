package com.example.proyectofinal.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal.data.api.RetrofitClient
import com.example.proyectofinal.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(RetrofitClient.authApi, RetrofitClient.getTokenManager())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.state.observe(this) { state ->
            when (state) {
                is RegisterState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                    binding.tvError.visibility = View.GONE
                }
                is RegisterState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Registro exitoso, bienvenido ${state.user.nombreCompleto}", Toast.LENGTH_LONG).show()
                    finish()
                }
                is RegisterState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    binding.tvError.text = state.message
                    binding.tvError.visibility = View.VISIBLE
                }
                RegisterState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    binding.tvError.visibility = View.GONE
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val nombres = binding.etNombres.text.toString().trim()
            val apellidos = binding.etApellidos.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()

            if (username.isEmpty()) {
                binding.tilUsername.error = "Ingrese su usuario"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.tilPassword.error = "Ingrese su contrasena"
                return@setOnClickListener
            }
            if (nombres.isEmpty()) {
                binding.tilNombres.error = "Ingrese sus nombres"
                return@setOnClickListener
            }
            if (apellidos.isEmpty()) {
                binding.tilApellidos.error = "Ingrese sus apellidos"
                return@setOnClickListener
            }

            clearErrors()

            viewModel.register(username, password, nombres, apellidos, email)
        }
    }

    private fun clearErrors() {
        binding.tilUsername.error = null
        binding.tilPassword.error = null
        binding.tilNombres.error = null
        binding.tilApellidos.error = null
        binding.tilEmail.error = null
    }
}
