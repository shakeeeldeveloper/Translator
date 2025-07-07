package com.example.translatorproject

import android.os.Bundle
import android.provider.Settings
import android.widget.CompoundButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.appThemeSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                
                //binding.imgAfterTrans.setImageBitmap(bitmap)
            } else {
               // binding.imgAfterTrans.setImageBitmap(originalBitmap)
            }
        }

    }
}
