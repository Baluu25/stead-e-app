package com.steade.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.steade.app.R
import com.steade.app.data.SessionManager
import com.steade.app.databinding.ActivityMainBinding
import com.steade.app.network.RetrofitClient
import com.steade.app.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        RetrofitClient.token = session.token

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        binding.bottomNav.setupWithNavController(navController)
    }

    fun logout() {
        session.clear()
        RetrofitClient.token = null
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }
}
