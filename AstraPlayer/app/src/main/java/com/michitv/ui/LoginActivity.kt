package com.michitv.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.michitv.databinding.ActivityLoginBinding
import com.michitv.network.AuthManager
import com.michitv.network.LoginResult
import com.michitv.util.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Si ya hay sesión guardada, ir directo
        val savedUser = PreferencesManager.getLoggedUser(this)
        if (savedUser != null) {
            goToMain(savedUser)
            return
        }

        binding.btnLogin.setOnClickListener {
            val user = binding.etUsername.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Ingresa usuario y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            doLogin(user, pass)
        }
    }

    private fun doLogin(username: String, password: String) {
        binding.progressLogin.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false

        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                AuthManager.login(username, password)
            }
            binding.progressLogin.visibility = View.GONE
            binding.btnLogin.isEnabled = true

            when (result) {
                is LoginResult.Success -> {
                    PreferencesManager.saveLoggedUser(this@LoginActivity, username)
                    goToMain(username)
                }
                is LoginResult.AccountDisabled -> {
                    Toast.makeText(this@LoginActivity,
                        "❌ Cuenta deshabilitada. Contacta al administrador.", Toast.LENGTH_LONG).show()
                }
                is LoginResult.InvalidCredentials -> {
                    Toast.makeText(this@LoginActivity,
                        "❌ Usuario o contraseña incorrectos.", Toast.LENGTH_SHORT).show()
                }
                is LoginResult.NetworkError -> {
                    Toast.makeText(this@LoginActivity,
                        "⚠️ Error de conexión. Verifica tu internet.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goToMain(username: String) {
        startActivity(Intent(this, MainActivity::class.java).apply {
            putExtra("username", username)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}
