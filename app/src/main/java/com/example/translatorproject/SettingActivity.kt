package com.example.translatorproject

import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.CompoundButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.translatorproject.adapter.SettingAdapter
import com.example.translatorproject.databinding.ActivityMainBinding
import com.example.translatorproject.databinding.ActivitySettingBinding
import com.example.translatorproject.model.SettingModel

class SettingActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SettingAdapter
    private lateinit var binding: ActivitySettingBinding

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        sharedPref = getSharedPreferences("settings", MODE_PRIVATE)
        val isNightMode = sharedPref.getBoolean("night_mode", false)

        AppCompatDelegate.setDefaultNightMode(
            if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Set switch state without triggering listener
        binding.appThemeSwitch.setOnCheckedChangeListener(null)
        binding.appThemeSwitch.isChecked = isNightMode


        binding.appThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val currentNightMode = AppCompatDelegate.getDefaultNightMode()

            if (isChecked && currentNightMode != AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveThemePreference(true)
                recreate() // Only recreate if the theme is changing
            } else if (!isChecked && currentNightMode != AppCompatDelegate.MODE_NIGHT_NO) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveThemePreference(false)
                recreate() // Only recreate if the theme is changing
            }
        }


        binding.settingRecyclerView.layoutManager = LinearLayoutManager(this)

        val itemList = listOf(
            SettingModel("Change Language", R.drawable.changelang_icon),
            SettingModel("Bookmark", R.drawable.bookmark_icon),
            SettingModel("History", R.drawable.history_icon),
            SettingModel("Rate Us", R.drawable.rate_us),
            SettingModel("Share App", R.drawable.share_icon),
            SettingModel("Customer Support", R.drawable.customer_support),
            SettingModel("About App", R.drawable.about_us)

        )

        adapter = SettingAdapter(itemList)
        binding.settingRecyclerView.adapter = adapter



    }
    private fun saveThemePreference(isNight: Boolean) {
        val editor = sharedPref.edit()
        editor.putBoolean("night_mode", isNight)
        editor.apply()
    }
}
