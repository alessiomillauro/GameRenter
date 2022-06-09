package com.android.gamerenter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val menu = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        menu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> findNavController(R.id.nav_host_fragment).navigate(R.id.dashboardFragment)
                R.id.search -> findNavController(R.id.nav_host_fragment).navigate(R.id.searchVideogameFragment)
                R.id.rent -> findNavController(R.id.nav_host_fragment).navigate(R.id.rentedVideogameFragment)
            }
            return@setOnItemSelectedListener true
        }
    }


}