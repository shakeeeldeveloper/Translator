package com.example.translatorproject

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.translatorproject.ui.chat.ChatFragment
import com.example.translatorproject.ui.home.HomeFragment
import com.example.translatorproject.ui.camera.CameraFragment
import com.example.translatorproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = getSharedPreferences("settings", MODE_PRIVATE)
        val isNightMode = sharedPref.getBoolean("night_mode", false)

        AppCompatDelegate.setDefaultNightMode(
            if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Default fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, HomeFragment())
            .commit()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, HomeFragment())
                        .commit()
                    true
                }
                R.id.navigation_chat -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, ChatFragment())
                        .commit()
                    true
                }
                R.id.navigation_camera -> {
                    // Launch CameraActivity instead of loading a fragment
                    val intent = Intent(this, CameraActivity::class.java)
                    startActivity(intent)
                    false // return false to prevent item being "selected"
                }
                else -> false
            }
        }

        /*binding.bottomNavigation.setOnItemSelectedListener { item ->
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
        }*/

    }
}
