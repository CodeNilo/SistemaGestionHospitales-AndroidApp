package com.example.proyectofinal.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinal.MainActivity
import com.example.proyectofinal.data.api.RetrofitClient
import com.example.proyectofinal.databinding.ActivityLoginBinding
import com.example.proyectofinal.ui.login.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(RetrofitClient.authApi, RetrofitClient.getTokenManager())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Si ya está logueado, ir directo a MainActivity
        if (RetrofitClient.getTokenManager().isLoggedIn()) {
            navigateToMain()
            return
        }

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                    binding.tvError.visibility = View.GONE
                }
                is LoginState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Bienvenido ${state.user.nombreCompleto}", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                is LoginState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    binding.tvError.text = state.message
                    binding.tvError.visibility = View.VISIBLE
                }
                LoginState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    binding.tvError.visibility = View.GONE
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty()) {
                binding.tilUsername.error = "Ingrese su usuario"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.tilPassword.error = "Ingrese su contrasena"
                return@setOnClickListener
            }

            binding.tilUsername.error = null
            binding.tilPassword.error = null

            viewModel.login(username, password)
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
