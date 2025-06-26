package com.example.translatorproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.translatorproject.ui.chat.ChatFragment
import com.example.translatorproject.ui.home.HomeFragment
import com.example.translatorproject.ui.camera.CameraFragment
import com.example.translatorproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Default fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, HomeFragment())
            .commit()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.navigation_home -> HomeFragment()
                R.id.navigation_chat -> ChatFragment()
                R.id.navigation_camera -> CameraFragment()
                else -> null
            }
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, it)
                    .commit()
                true
            } ?: false
        }
    }
}
