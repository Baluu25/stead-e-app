package com.steade.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.steade.app.data.SessionManager
import com.steade.app.databinding.ActivityRegisterBinding
import com.steade.app.network.RetrofitClient
import com.steade.app.ui.MainActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        binding.btnRegister.setOnClickListener { register() }
        binding.tvLogin.setOnClickListener { finish() }
    }

    private fun register() {
        val name = binding.etName.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirm = binding.etConfirmPassword.text.toString()

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirm) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.register(
                    mapOf(
                        "name" to name,
                        "username" to username,
                        "email" to email,
                        "password" to password,
                        "password_confirmation" to confirm
                    )
                )
                if (response.isSuccessful) {
                    val body = response.body()!!
                    session.token = body.token
                    session.userId = body.user.id
                    session.userName = body.user.name
                    session.userEmail = body.user.email
                    RetrofitClient.token = body.token
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finishAffinity()
                } else {
                    Toast.makeText(this@RegisterActivity, "Registration failed. Check your details.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Could not connect to server", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.btnRegister.isEnabled = !loading
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}
