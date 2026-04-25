package com.steade.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.steade.app.data.SessionManager
import com.steade.app.databinding.ActivityLoginBinding
import com.steade.app.network.RetrofitClient
import com.steade.app.ui.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        if (session.isLoggedIn()) {
            RetrofitClient.token = session.token
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding.btnLogin.setOnClickListener { login() }
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.login(
                    mapOf("email" to email, "password" to password)
                )
                if (response.isSuccessful) {
                    val body = response.body()!!
                    session.token = body.token
                    session.userId = body.user.id
                    session.userName = body.user.name
                    session.userEmail = body.user.email
                    RetrofitClient.token = body.token
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Could not connect to server", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.btnLogin.isEnabled = !loading
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}
